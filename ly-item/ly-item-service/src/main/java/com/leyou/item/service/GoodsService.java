package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/4
 * @Description: 关于商品类表的service
 * @version: 1.0
 */
@Slf4j
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;


    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     * 分页查询spu
     *
     * @param key      查询条件
     * @param saleable 商品是否上架
     * @param page     从第几页开始
     * @param rows     每页查询多少条数据
     * @return
     */
    public PageResult<Spu> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {

        //分页,采用分页助手
        PageHelper.startPage(page, rows);

        //Example对象用来封装查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //过滤1：查询条件key
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        //过滤2：上下架saleable
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //默认排序,按商品更新时间倒序排
        example.setOrderByClause("last_update_time DESC");

        //只查询有效的商品,数据库字段valid= 1
        criteria.andEqualTo("valid",true);

        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);

        //没查到抛出异常
        if (CollectionUtils.isEmpty(spus)) {
            throw new LyExcception(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //解析分类名称和品牌名称,因为查询的只是分类id和品牌id,而页面需要对应的名称
        List<Spu> spusList = loadCategoryAndBrand(spus);


        //解析分页结果
        PageInfo<Spu> pageInfo = new PageInfo<Spu>(spusList);

        //获取数据总条数
        long total = pageInfo.getTotal();

        //获取总页数
        long totalPages = pageInfo.getPages();


        return new PageResult<Spu>(total, totalPages, spus);
    }


    private List<Spu> loadCategoryAndBrand(List<Spu> spus) {

        for (Spu spu : spus) {

            //处理分类名称
            List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());

            //根据多个id查询分类
            List<Category> categories = categoryService.queryByCids(cids);

            //java8新特性
            List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

            spu.setCname(StringUtils.join(names, "/"));//将集合中的name的值以/进行拼接

            //处理品牌名称
            Brand brand = brandService.queryBrandById(spu.getBrandId());
            spu.setBname(brand.getName());

        }
        return spus;
    }

    /**
     * 比较笨的方法
     * 根据分类id和品牌id解析对应的名称
     * @param spus
     */
    private List<Spu> loadCategoryAndBrand2(List<Spu> spus) {

        for (Spu spu : spus) {
            //获取类目1级名称
            Category c1 = categoryMapper.selectByPrimaryKey(spu.getCid1());
            //获取类目2级名称
            Category c2 = categoryMapper.selectByPrimaryKey(spu.getCid2());
            //获取类目3级名称
            Category c3 = categoryMapper.selectByPrimaryKey(spu.getCid3());

            //设置类名的层级机构名称
            spu.setCname(c1.getName() + "/" + c2.getName() + "/" + c3.getName());

            //获取品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spu.setBname(brand.getName());
        }
        return spus;
    }

    /**
     * 新增商品
     *
     * @param spu
     */
    @Transactional
    public void addSpu(Spu spu) {
        //新增spu表
        spu.setCreateTime(new Date());    //创建时间
        spu.setLastUpdateTime(new Date());//最后一次更新时间
        spu.setSaleable(true);            //新增商品默认为上架状态
        spu.setValid(true);               //新增商品默认有效
        int spuCount = spuMapper.insert(spu);
        if (spuCount != 1) {
            throw new LyExcception(ExceptionEnum.ADD_SPU_ERROR);
        }

        //新增spuDetail表
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());      //该表的主键来自于spu表的主键
        int detailCount = spuDetailMapper.insert(spuDetail);
        if (detailCount != 1) {
            throw new LyExcception(ExceptionEnum.ADD_SPU_DETAIL_ERROR);
        }

        //新增sku表
        List<Sku> skus = spu.getSkus();

        //定义一个集合用来存储Stock对象，为批量新增Stock做准备
        List<Stock> stocks = new ArrayList<>();

        /**
         * 没有对sku进行批量新增的原因是：批量新增不返回新增的id
         * 而stock需要sku的id
         */
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());    //创建时间
            sku.setLastUpdateTime(new Date());//最后一次更新时间
            sku.setSpuId(spu.getId());        //所属spuid
            int skuCount = skuMapper.insert(sku);
            if (skuCount != 1) {
                throw new LyExcception(ExceptionEnum.ADD_SKU_ERROR);
            }

            //将stock放到集合中
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }

        //批量新增
        int stockCount = stockMapper.insertList(stocks);
        //传入欲新增的stock个数和批量新增后返回的个数不一致时，抛出异常失败！
        if (stockCount != stocks.size()) {
            throw new LyExcception(ExceptionEnum.ADD_STOCK_ERROR);
        }

        //发送MQ消息
        sendMQMessage(spu.getId(),"insert");

    }

    /**
     * 新增商品 :没有使用批量新增，而是使用循环新增的笨方法！！！
     * @param spu
     */
    @Transactional
    public void addSpu2(Spu spu) {
        //新增spu表
        spu.setCreateTime(new Date());    //创建时间
        spu.setLastUpdateTime(new Date());//最后一次更新时间
        spu.setSaleable(true);            //新增商品默认为上架状态
        spu.setValid(true);               //新增商品默认有效
        int spuCount = spuMapper.insert(spu);
        if (spuCount != 1) {
            throw new LyExcception(ExceptionEnum.ADD_SPU_ERROR);
        }

        //新增spuDetail表
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());      //该表的主键来自于spu表的主键
        int detailCount = spuDetailMapper.insert(spuDetail);
        if (detailCount != 1) {
            throw new LyExcception(ExceptionEnum.ADD_SPU_DETAIL_ERROR);
        }

        //新增sku表
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());    //创建时间
            sku.setLastUpdateTime(new Date());//最后一次更新时间
            sku.setSpuId(sku.getId());        //所属spuid
            int skuCount = skuMapper.insert(sku);
            if (skuCount != 1) {
                throw new LyExcception(ExceptionEnum.ADD_SKU_ERROR);
            }

            //新增stock表
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            int stockCount = stockMapper.insert(stock);
            if (stockCount != 1) {
                throw new LyExcception(ExceptionEnum.ADD_STOCK_ERROR);
            }
        }
    }

    /**
     * 根据spuid查询spudetail
     * @param spuid
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuid) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuid);
        if (spuDetail == null) {
            throw new LyExcception(ExceptionEnum.SUP_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    /**
     * 根据spuid查询sku
     * @param id
     * @return
     */
    public List<Sku> querySkuBySpuId(Long id) {
        //查询条件
        Sku sku = new Sku();
        sku.setSpuId(id);

        List<Sku> skus = skuMapper.select(sku);

        if (CollectionUtils.isEmpty(skus)) {
            throw new LyExcception(ExceptionEnum.SKU_NOT_FOUND);
        }

        for (Sku s : skus) {
            //从stock表中获取库存量
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        }
        return skus;
    }

    /**
     * 修改商品 注意一点：
     * spu数据可以修改，但是SKU数据无法修改，因为有可能之前存在的SKU现在已经不存在了，
     * 或者以前的sku属性都不存在了。比如以前内存有4G，现在没了。
     * 因此这里直接删除以前的SKU，然后新增即可。
     * @param spu
     */
    @Transactional
    public void updateSpu(Spu spu) {
        //修改spu表
        spu.setLastUpdateTime(new Date());//最后一次更新时间
        int spuCount = spuMapper.updateByPrimaryKeySelective(spu);
        if (spuCount != 1) {
            throw new LyExcception(ExceptionEnum.UPDATE_SPU_ERROR);
        }

        //修改spuDetail表
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());      //该表的主键来自于spu表的主键
        int detailCount = spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        if (detailCount != 1) {
            throw new LyExcception(ExceptionEnum.UPDATE_SPU_DETAIL_ERROR);
        }

        //根据spu_id查询所有的sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skusList = skuMapper.select(sku);

        //用于存放sku的id
        List<Long> skuIds = new ArrayList<>();

        //当sku存在时，我们直接将其删除掉
        if (!CollectionUtils.isEmpty(skusList)) {
            for (Sku sku1 : skusList) {
                skuIds.add(sku1.getId());
            }
            //批量删除sku
            int delSkuCount = skuMapper.deleteByIdList(skuIds);
            if (delSkuCount != skuIds.size()) {
                throw new LyExcception(ExceptionEnum.DEL_SKU_ERROR);
            }
            //批量删除stock
            int delStockCount = stockMapper.deleteByIdList(skuIds);
            if (delStockCount != skuIds.size()) {
                throw new LyExcception(ExceptionEnum.DEL_STOCK_ERROR);
            }
        }

        //数据库不存在sku时，新增sku表
        List<Sku> skus = spu.getSkus();

        //定义一个集合用来存储Stock对象，为批量新增Stock做准备
        List<Stock> stocks = new ArrayList<>();

        /**
         * 没有对sku进行批量新增的原因是：批量新增不返回新增的id
         * 而stock需要sku的id
         */
        for (Sku sku2 : skus) {
            sku2.setCreateTime(new Date());    //创建时间
            sku2.setLastUpdateTime(new Date());//最后一次更新时间
            sku2.setSpuId(spu.getId());        //所属spuid
            int skuCount = skuMapper.insert(sku2);
            if (skuCount != 1) {
                throw new LyExcception(ExceptionEnum.ADD_SKU_ERROR);
            }
            //将stock放到集合中
            Stock stock = new Stock();
            stock.setSkuId(sku2.getId());
            stock.setStock(sku2.getStock());
            stocks.add(stock);
        }

        //批量新增
        int stockCount = stockMapper.insertList(stocks);
        //传入欲新增的stock个数和批量新增后返回的个数不一致时，抛出异常失败！
        if (stockCount != stocks.size()) {
            throw new LyExcception(ExceptionEnum.ADD_STOCK_ERROR);
        }

        //发送MQ消息
        sendMQMessage(spu.getId(),"update");

    }

    /**
     * 根据spuid修改上下架
     * @param spuid
     * @param saleable
     */
    public void updateSaleable(Long spuid, Boolean saleable) {
        //修改条件
        Spu spu = new Spu();
        spu.setId(spuid);
        spu.setSaleable(saleable);

        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyExcception(ExceptionEnum.UPDATE_SALEABLE_ERROR);
        }

        //上/下架商品需要发送MQ消息到es微服务和静态化微服务
        if(saleable){
            sendMQMessage(spuid,"insert");
        }else {
            sendMQMessage(spuid,"delete");
        }
    }

    /**
     * 发送MQ消息
     * @param id   商品spuid
     * @param type routinkey的标识
     * 无需指定交换机，因此默认发送到了配置中的：leyou.item.exchange
     * 这里要把所有异常都try起来，不能让消息的发送影响到正常的业务逻辑
     */
    private void sendMQMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    /**
     * 根据spuid删除商品
     *
     * @param spuid
     */
    @Transactional
    public void delGoodsById(Long spuid) {

        //逻辑删除商品
        Spu spu = new Spu();
        spu.setId(spuid);
        spu.setValid(false);
        spuMapper.updateByPrimaryKeySelective(spu);

        //发送MQ消息
        //删除商品需要发送MQ消息到es微服务和静态化微服务
        sendMQMessage(spuid,"delete");

        //物理删除商品
        /*
        //根据spuid删除 spu
        spuMapper.deleteByPrimaryKey(spuid);

        //根据spuid删除 spudetail
        spuDetailMapper.deleteByPrimaryKey(spuid);

        //根据spuid查询所有sku
        Sku sku = new Sku();
        sku.setSpuId(spuid);

        List<Sku> skus = skuMapper.select(sku);

        //用于存放sku的id
        List<Long> skuIds = new ArrayList<>();
        for (Sku skus1 : skus) {
            skuIds.add(skus1.getId());
        }
        //根据skuid批量删除 stock
        int count = stockMapper.deleteByIdList(skuIds);
        if (count != skuIds.size()) {
            throw new LyExcception(ExceptionEnum.DEL_STOCK_ERROR);
        }

        //根据spuid删除 sku
        int count2 = skuMapper.deleteByIdList(skuIds);
        if (count2 != skuIds.size()) {
            throw new LyExcception(ExceptionEnum.DEL_SKU_ERROR);
        }
       */
    }

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        //根据spuid查询spu表相关信息
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw  new LyExcception(ExceptionEnum.SUP_NOT_FOUND);
        }

        //查询spu详情
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spu.getId());
        if(spuDetail==null){
            throw  new LyExcception(ExceptionEnum.SUP_DETAIL_NOT_FOUND);
        }
        spu.setSpuDetail(spuDetail);

        //查询sku表
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skus = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skus)){
            throw  new LyExcception(ExceptionEnum.SKU_NOT_FOUND);
        }

        for (Sku s : skus) {
            //查询stock库存
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        }

        spu.setSkus(skus);
        return spu;
    }

    /**
     * 根据多个sku的id查询sku集合
     * @param ids
     * @return
     */
    public List<Sku> querySkusBySkuIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(skus)){
            throw  new LyExcception(ExceptionEnum.SKU_NOT_FOUND);
        }

        for (Sku sku : skus) {
            //查询stock库存
            Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        }

        return skus;
    }
    /**
     *  减库存
     * @param cartDtos
     * @return
     */
    @Transactional
    public void decreaseStock(List<CartDto> cartDtos) {
        for (CartDto cartDto : cartDtos) {
            int count = stockMapper.decreaseStock(cartDto.getSkuId(), cartDto.getNum());
            if (count!=1){
                log.error("[商品微服务],库存不足！");
                throw new LyExcception(ExceptionEnum.NO_STOCK_ERROR);
            }
        }
    }
}
