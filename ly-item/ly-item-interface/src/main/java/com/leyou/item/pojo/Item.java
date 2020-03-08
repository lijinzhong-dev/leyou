package com.leyou.item.pojo;

import lombok.Data;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description: 使用lombok的@Data注解生成setter/getter、构造方法等
 * @version: 1.0
 */
@Data
public class Item {
    private  Integer id;
    private String name;
    private  Long price;
}
