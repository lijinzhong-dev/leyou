package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;    // 所属品牌id
    private Long cid1;       // 1级类目
    private Long cid2;       // 2级类目
    private Long cid3;       // 3级类目
    private String title;    // 标题
    private String subTitle; // 子标题
    private Boolean saleable;// 是否上架
    private Boolean valid;   // 是否有效，逻辑删除用
    private Date createTime; // 创建时间

    @JsonIgnore   //添加该注解的属性不会返回到前端页面
    private Date lastUpdateTime;// 最后修改时间

    @Transient //表示该属性并非一个到数据库表的字段的映射,
    private String cname;// 商品分类名称
    @Transient //表示该属性并非一个到数据库表的字段的映射,
    private String bname;// 品牌名称

    @Transient
    private SpuDetail spuDetail; // 注意：名称要和前端传递的json参数的key一致才能接受到

    @Transient
    private List<Sku> skus;   //  注意：名称要和前端传递的json参数的key一致才能接受到
}