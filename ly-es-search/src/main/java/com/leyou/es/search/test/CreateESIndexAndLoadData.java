package com.leyou.es.search.test;

import com.leyou.common.vo.PageResult;
import com.leyou.es.search.client.GoodsClient;
import com.leyou.es.search.pojo.Goods;
import com.leyou.es.search.repository.GoodsRepository;
import com.leyou.es.search.service.ESSearchService;
import com.leyou.item.pojo.Spu;
import org.elasticsearch.search.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 为实体类Goods创建其ES的索引库，只执行一次即可
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateESIndexAndLoadData {
    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private ESSearchService esSearchService;

    @Autowired
    private GoodsRepository goodsRepository;

    /**
     * 创建索引库和创建映射关系
     */
    @Test
    public void createESIndex() {
        //创建索引库
        template.createIndex(Goods.class);

        //创建映射关系
        template.putMapping(Goods.class);
    }


    /**
     * 从数据库中查询Spu,并将其导入到ES索引库中
     */
    @Test
    public void loadDatatoEs() {
        String key = null;
        boolean saleable = true; // 上架商品
        int page = 1; //当前页,从1开始，而es的页码是从0开始
        int rows = 100;
        //查询结果总页数
        long totalPage = 0L;
        do {
            PageResult<Spu> spuPageResult = goodsClient.querySpuByPage(key, saleable, page, rows);

            totalPage = spuPageResult.getTotalPage();

            List<Spu> spus = spuPageResult.getItems();

            if (CollectionUtils.isEmpty(spus)) {
                break;
            }

            //存储goods的集合
            List<Goods> goodsList = new ArrayList<>();

            for (Spu spu : spus) {
                //将spu处理成goods
                Goods goods = esSearchService.buildGoods(spu);
                goodsList.add(goods);
            }

            //批量新增数据（文档）到es索引库
            goodsRepository.saveAll(goodsList);

            page++;
        } while (page <= totalPage);
        System.out.println("数据导入es成功!");
    }
}