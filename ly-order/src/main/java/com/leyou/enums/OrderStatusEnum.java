package com.leyou.enums;

import lombok.Data;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: 订单状态的枚举
 * @version: 1.0
 */
public enum  OrderStatusEnum {
    //1、未付款 2、已付款,未发货 3、已发货,未确认 4、交易成功 5、交易关闭 6、已评价
    UN_PAY(1,"未付款"),
    PAYED(2,"已付款,未发货"),
    DELIVERED(3,"已发货,未确认"),
    SUCCESS(4,"交易成功"),
    CLOSED(5,"交易关闭"),
    RATED(6,"已评价"),

    ;
    private  int statusCode; //订单状态码
    private  String codeDesc;//订单状态码描述

    OrderStatusEnum(int statusCode, String codeDesc) {
        this.statusCode = statusCode;
        this.codeDesc = codeDesc;
    }

    public int value(){
        return this.statusCode;
    }
}
