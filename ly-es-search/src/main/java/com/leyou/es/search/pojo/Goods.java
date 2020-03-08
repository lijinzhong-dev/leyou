package com.leyou.es.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class Goods {
    @Id
    private Long id; // spuId
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌、规格参数值信息
    
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;// 卖点

    //未加@Field，spring也可以进行推测进行自动添加
    private Long brandId;// 品牌id
    private Long cid1;// 1级分类id
    private Long cid2;// 2级分类id
    private Long cid3;// 3级分类id
    private Date createTime;// 创建时间(用于过滤，点击'新品'就是按该字段排序)
    private Set<Long> price;// 价格  集合对应es中的是数组
   
    @Field(type = FieldType.Keyword, index = false)
    private String skus;// sku信息的json结构，用于页面展示
   
    private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值
}