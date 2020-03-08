package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/2
 * @Description: com.leyou.item.pojo
 * @version: 1.0
 */
@Data
@Table(name = "tb_spec_param")
public class SpecParam {
    @Id
    @KeySql(useGeneratedKeys = true)
    private  Long id;            // 主键
    private  Long cid;           //商品分类id
    private  Long groupId;       //规格组id
    private  String name;        //规格参数名称
    private  Boolean numericl;    //是否是数字类型参数，true或false
    private  String unit;        //数字类型参数的单位，非数字类型可以为空
    private  Boolean generic;    //是否是sku通用属性，true或false
    private  Boolean searching;  //是否用于搜索过滤，true或false
    private  String segments;    //当为数值类型参数时，如果需要搜索，则添加分段间隔值，如CPU频率间隔：0.5-1.0
}
