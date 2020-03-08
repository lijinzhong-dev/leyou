package com.leyou.common.test.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/22
 * @Description: 测试JsontUtils工具类使用到的实体类
 * @version: 1.0
 */
@Data
@AllArgsConstructor //全参构造函数
@NoArgsConstructor  //无参构造函数
public  class User{

    private String name;

    private Integer age;
}
