package com.leyou.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.dto
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long skuId; // 商品skuId
    private Integer num;// 购买数量
}
