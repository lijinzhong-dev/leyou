package com.leyou.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 收货人地址信息
 */
@Data
@Table(name = "tb_receiver")
public class ReceiverAddress {
    @Id
    private Long id;
    private Long loginId; //登录者id
    private String name;// 收件人姓名
    private String phone;// 电话
    private String state;// 省份
    private String city;// 城市
    private String district;// 区
    private String address;// 街道地址
    private String  zipCode;// 邮编
    private Boolean isDefault; //是否是默认收获地址
}