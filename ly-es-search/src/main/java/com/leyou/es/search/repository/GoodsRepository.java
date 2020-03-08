package com.leyou.es.search.repository;

import com.leyou.es.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description:  继承接口ElasticsearchRepository
 *                为实体类Goods生成ES相关的增删改查操作
 * @version: 1.0
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
