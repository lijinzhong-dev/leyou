package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/2
 * @Description: 商品规格组实体类
 * @version: 1.0
 */
@Data
@Table(name = "tb_spec_group")
public class SpecGroup {
    @Id
    @KeySql(useGeneratedKeys = true)
    private  Long id;//规格组id

    private  Long cid;//所属商品分类id

    private  String name;//规格组名称

    @Transient
    private List<SpecParam> params; // 该组下的所有规格参数集合
}
