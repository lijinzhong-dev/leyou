package com.leyou.es.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.es.search.client.BrandClient;
import com.leyou.es.search.client.CategoryClient;
import com.leyou.es.search.client.GoodsClient;
import com.leyou.es.search.client.SpecificationClient;
import com.leyou.es.search.pojo.Goods;
import com.leyou.es.search.pojo.SearchRequest;
import com.leyou.es.search.pojo.SearchResult;
import com.leyou.es.search.repository.GoodsRepository;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregator;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description: com.leyou.es.search.service
 * @version: 1.0
 */
@Service
public class ESSearchService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specClient;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsRepository goodsRepository;

    /**
     *  将查询到的Spu对象处理成Goods
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu){
        Goods goods = new Goods();

        //查询分类
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<Category> categories = categoryClient.queryCategoryByCids(cids);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        //jdk8新特性 获取对象集合List<Category>中Category的name值，封装成一个List集合
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //将集合names中的元素用空格分割后拼接成一个字符串
        String categoryNameString = StringUtils.join(names, " ");

        //查询品牌
        Brand brand = brandClient.queryBrandByBid(spu.getBrandId());
        if (brand==null){
            throw new LyExcception(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //将商品标题、分类名称、品牌名称、拼接成搜索字段,后期用户的搜索就是从这里搜索的
        String all = spu.getTitle() + categoryNameString + brand.getName();
        //搜索字段：包含标题名称、品牌名称、规格、分类名称等
        goods.setAll(all);

        //查询spu下的所有sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skus)){
            throw new LyExcception(ExceptionEnum.SKU_NOT_FOUND);
        }
        Set<Long> prices = skus.stream().map(Sku::getPrice).collect(Collectors.toSet());
        //spu下的所有sku的价格集合
        goods.setPrice(prices);

        //对查询到的List<Sku> skus进行处理,处理成包含我们所需的字段
        List<Map<String,Object>> newSkus=new ArrayList<>();
        for (Sku sku : skus) {
            Map<String,Object> map=new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("createTime",sku.getCreateTime());
            //StringUtils.substringBefore(sku.getImages(),","))表示获取第一个图片的url
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));
            newSkus.add(map);
        }
        //使用工具类将包含我们所需字段的List<Map<String,Object>> newSkus序列化成json
        String skusJson = JsonUtils.toString(newSkus);
        //所有sku的集合的json格式
        goods.setSkus(skusJson);

        //查询可以搜索的规格参数(不需要传规格分组id,因为一个分类就对应一套规格参数列表)
        List<SpecParam> params = specClient.querySpecParamByList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)){
            throw new LyExcception(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //根据spuid查询商品详情spudetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        //获取通用规格参数即表tb_spu_detail中字段generic_spec的值,json形式如：{"1":"其它","2":"G9青春版（全网通版）"}
        String genericSpecJson = spuDetail.getGenericSpec();
        //将genericSpecJson转成Map<String,String>
        Map<String, String> genericSpec = JsonUtils.toMap(genericSpecJson, String.class, String.class);

        //获取特有规格参数即表tb_spu_detail中字段special_spec的值,json形式如：{"4":["白色","金色","玫瑰金"],"12":["3GB"],"13":["16GB"]}
        String specialSpecJson = spuDetail.getSpecialSpec();
        //将specialSpecJson转成Map<String,List<String>>
        Map<String, List<String>> specialSpec = JsonUtils.nativeRead(specialSpecJson, new TypeReference<Map<String, List<String>>>(){});
        //用于封装规格参数的map，key是规格参数的名称,值是商品详情的相关值
        Map<String, Object> specs =new HashMap<>();
        for (SpecParam param : params) {
            //规格参数的名称
            String key = param.getName();
            Object value ="";
            //判断是否是通用规格
            if (param.getGeneric()) {

                value = genericSpec.get(param.getId().toString());
                //判断是否是数值类型
                if(value!=null && param.getNumericl()){
                    //如果是数值类型需要将其处理成一个区间段
                    value = chooseSegment(value.toString(), param);
                }
            }else {
                //特有规格对应的值是集合,无需处理区间段问题
                value = specialSpec.get(param.getId().toString());
            }
            //存入map
            specs.put(key,value);
        }

        //所有可搜索的规格参数
        goods.setSpecs(specs);


        //商品id
        goods.setId(spu.getId());

        //商品品牌id
        goods.setBrandId(spu.getBrandId());

        //商品分类id
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());

        //商品子标题
        goods.setSubTitle(spu.getSubTitle());

        //商品创建时间
        goods.setCreateTime(spu.getCreateTime());

        return goods;
    }

    /**
     *  判断数值类型的规格参数值所对应的的区间段
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 分页从es索引库中查询商品信息
     * @param searchRequest
     * @return
     */
    public PageResult<Goods> queryByPage(SearchRequest searchRequest) {
        int page = searchRequest.getPage();//获取当前页码
        int pageSize = searchRequest.getSize();//获取每页显示记录数
        String key = searchRequest.getKey();//获取用户输入的查询关键字

        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //分页 注意：elasticsearch的第一页从0开始，所以必须减1 即：page-1
        queryBuilder.withPageable(PageRequest.of(page-1,pageSize));

        //获取排序字段
        String sortBy = searchRequest.getSortBy();
        //获取排序方式，即是否降序
        Boolean desc = searchRequest.getDescending();
        if(StringUtils.isNotBlank(sortBy)){
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc?SortOrder.DESC:SortOrder.ASC));
        }

        //结果过滤：指定要查询哪些字段
        String[] includes = {"id","subTitle","skus"};
        String[] excludes =null ;
        queryBuilder.withSourceFilter(new FetchSourceFilter(includes,null));

        //根据用户输入的查询条件过滤
        /**
         * 构建一个基本的查询，注意：查询条件和过滤条件是不一样的
         */
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        queryBuilder.withQuery(basicQuery);

        //在搜索之前进行按商品分类和品牌进行聚合,为了在页面上展示商品分类和品牌
        //商品分类聚合 按字段cid3聚合，聚合的名称叫做categories_agg
        queryBuilder.addAggregation(AggregationBuilders.terms("categories_agg").field("cid3"));
        //商品品牌聚合 按字段brandId聚合，聚合的名称叫做brand_agg
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));


        //执行一般搜索可以使用GoodsRepository 也可以使用 ElasticsearchTemplate
        //Page<Goods> pageInfo = goodsRepository.search(queryBuilder.build());

        //搜索聚合的结果要用 ElasticsearchTemplate
        AggregatedPage<Goods> pageInfo = template.queryForPage(queryBuilder.build(), Goods.class);


        // 解析分页结果
        long totalPages = pageInfo.getTotalPages();//总页数
        long totalElements = pageInfo.getTotalElements();//总条数
        List<Goods> content = pageInfo.getContent();//当前页的数据

        //解析聚合结果
        //获取所有聚合结果
        Aggregations aggs = pageInfo.getAggregations();

        //解析商品分类的聚合结果
        List<Category> categories = parseCategoryAgg(aggs.get("categories_agg"));
        //解析商品品牌的聚合结果
        List<Brand> brands = parseBrandAgg(aggs.get("brand_agg"));

        //解析商品规格参数聚合后的结果：
        List<Map<String,Object>> specs =null;

        //根据商品分类聚合后，其聚合结果是否等于1，等于再进行商品规格参数的聚合操作
        if (categories!=null && categories.size() == 1){
            /**
             * 第一个参数：规格参数进行聚合操作基于的分类id
             * 第二个参数：规格参数进行聚合操作基于用户的查询条件
             */
            specs = parseSpecAgg(categories.get(0).getId(),basicQuery);
        }

        //封装查询的结果包括聚合结果
        PageResult<Goods> result = new SearchResult(totalElements, totalPages, content,categories,brands,specs);

        return  result;

    }

    /**
     * 构建一个基本查询 包括：
     *  1.用户的输入的关键字查询
     *  2.点击过滤项的过滤
     * @param searchRequest
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest searchRequest) {
        //创建布尔查询（包括查询条件 和 过滤条件）
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //查询条件 放到must中
        queryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()));

        //过滤条件 放到filter中
        //获取所有的过滤项
        Map<String, String> map = searchRequest.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();//过滤名称
            String value = entry.getValue();//过滤项的值
            //分析key 分为两种cid3和brandId是一类，该类是Long类型，规格参数的key是另一类，该类是字符串类型
            if(!"cid3".equals(key) && !"brandId".equals(key)){//当为规格参数的key时
                key="specs."+key+".keyword";
            }
            //因为过滤项的值要么是字符串，要么是Long类型,那么我们使用termQuery进行过滤
            queryBuilder.filter(QueryBuilders.termQuery(key,value));
        }
        return queryBuilder;
    }

    /**
     *  商品规格参数进行聚合
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String,Object>> parseSpecAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String,Object>> specs = new ArrayList<>();
        //1 获取需要聚合的规格参数
        List<SpecParam> specParams = specClient.querySpecParamByList(null, cid, true);

        //2 聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);//在原来用户的查询条件下进行商品规格的聚合操作
        for (SpecParam specParam : specParams) {
            String specName=specParam.getName();//获取参数名称
            queryBuilder.addAggregation(AggregationBuilders.terms(specName).field("specs."+specName+".keyword"));
        }
        //3 获取聚合结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        Aggregations aggs = result.getAggregations();//获取到所有的聚合结果

        //4 解析聚合结果
        for (SpecParam specParam : specParams) {
            String specName=specParam.getName();//获取参数名称
            StringTerms agg = aggs.get(specName);//根据规格参数名称获取对应的聚合结果

            List<StringTerms.Bucket> buckets = agg.getBuckets();//获取规格参数聚合桶
            //获取对应规格参数的待选项值 如：'4GB,6GB,8GB'
            List<String> options = buckets.stream().
                                  map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            //准备map
            Map<String,Object> map =new HashMap<>();
            map.put("k",specName);
            map.put("options",options);
            specs.add(map);
        }
        return  specs;
    }

    /**
     * 解析商品品牌的聚合结果
     * @param brand_agg
     * @return
     */
    private List<Brand> parseBrandAgg(LongTerms brand_agg) {
        try {
            //获取到按品牌聚合的所有的桶
            List<LongTerms.Bucket> buckets = brand_agg.getBuckets();
            //获取品牌id的集合
            List<Long> brandIds = buckets.stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());

            //根据品牌id的集合查询品牌信息
            List<Brand> brands = brandClient.queryBrandByBidList(brandIds);

            return brands;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * 解析商品分类的聚合结果
     * @param categories_agg  方法参数使用 Aggregation的子类来获取桶，
     *                        其实用什么类型的聚合生成桶，那么就用什么类型的聚合来获取桶
     * @return
     */
    private List<Category> parseCategoryAgg(LongTerms categories_agg) {
        try {
            //获取到按商品分类聚合的所有的桶
            List<LongTerms.Bucket> buckets = categories_agg.getBuckets();
            //获取商品分类id的集合
            List<Long> cids = buckets.stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());

            //根据商品分类id的集合查询分类信息
            List<Category> categories = categoryClient.queryCategoryByCids(cids);

            return categories;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * 根据分类级别3，即cid3，查询出1-3级别的分类用list集合包装
     * @param cid3
     * @return
     */
    public List<Category> queryCategoryByCid3(Long cid3) {
        List<Category> categories = categoryClient.queryCategoryByCid3(cid3);
        if(CollectionUtils.isEmpty(categories)){
            throw  new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    /**
     * 处理MQ消息，es索引库的新增或者修改
     * @param spuId
     */
    public void createOrUpdateIndex(Long spuId) {
        //查询spu实体信息
        Spu spu = goodsClient.querySpuById(spuId);
        if(spu==null){
            throw  new LyExcception(ExceptionEnum.SUP_NOT_FOUND);
        }

        //构建Goods
        Goods goods = buildGoods(spu);

        //新增或者修改索引
        goodsRepository.save(goods);
    }

    /**
     * 处理MQ消息，es索引库的索引删除
     * @param spuId
     */
    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
