package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description: 异常信息枚举
 * @version: 1.0
 */
@Getter             //只对该类的属性提供get方法
@NoArgsConstructor  //提供无参构造函数 枚举里的构造函数默认都是私有化的,也就是不能通过new创建枚举对象
@AllArgsConstructor //提供全参构造函数
public enum ExceptionEnum {
    /**
     * PRICE_CANNOT_BE_NULL(400,"价格不能为空!")等价于new ExceptionEnums(400,"价格不能为空!");
     *
     * 枚举规定 PRICE_CANNOT_BE_NULL(400,"价格不能为空!")对象必须放到该类的最前面
     * 多个枚举对象用逗号(,)分隔,最后一个枚举对象用分号(;)结束,
     * 且枚举对象名称不能重复（枚举对象名称可以任意自定义），如下：
     * PRICE_CANNOT_BE_NULL(400,"价格不能为空!"),
     * NAME_CANNOT_BE_NULL(400,"姓名不能为空!"),
     * ...
     * AGE_CANNOT_BE_NULL3(400,"年龄不能为空!");
     */
    PRICE_CANNOT_BE_NULL(400,"价格不能为空!"),//创建枚举对象
    CATEGORY_NOT_FOUND(404,"商品分类没查到!"),//创建枚举对象
    BRAND_NOT_FOUND(404,"商品品牌没查到!"),//创建枚举对象
    GOODS_NOT_FOUND(404,"商品信息没查到!"),
    ADD_BRAND_ERROR(500,"新增品牌失败!"),
    ADD_BRAND_CATEGORY_ERROR(500,"新增分类和品牌关系失败!"),
    UP_IAMGE_ERROR(500,"上传图片失败!"),
    NOT_ALLOW_FILE_TYPE(400,"不支持上传的文件类型!"),
    NOT_ALLOW_FILE_CONTENT(400,"不支持上传的文件类型!"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组不存在!"),
    HAS_SUB_TREE(400,"该节点下还有子节点!"),
    SPEC_PARAM_NOT_FOUND(404,"规格参数没查到!"),
    SUP_DETAIL_NOT_FOUND(404,"SUP_DETAIL没查到!"),
    SUP_NOT_FOUND(404,"SUP没查到!"),
    SKU_NOT_FOUND(404,"SKU没查到!"),
    ADD_SPU_ERROR(500,"新增SPU失败!"),
    UPDATE_SPU_ERROR(500,"更新SPU失败!"),
    ADD_SPU_DETAIL_ERROR(500,"新增SPU_DETAIL失败!"),
    UPDATE_SPU_DETAIL_ERROR(500,"修改SPU_DETAIL失败!"),
    ADD_SKU_ERROR(500,"新增SKU失败!"),
    UPDATE_SALEABLE_ERROR(500,"上下架失败!"),
    ADD_STOCK_ERROR(500,"新增STOCK失败!"),
    DEL_STOCK_ERROR(500,"删除STOCK失败!"),
    DEL_SKU_ERROR(500,"删除SKU失败!"),
    INVALID_USER_DATA_TYPE_ERROR(400,"非法的用户数据类型，数据类型只能是 1 或者 2!"),
    PHONE_CODE_ERROR(400,"输入的验证码错误！"),
    PHONE_CODE_EXPIRE(400,"输入的验证码已过期！"),
    USER_NOT_FOUND(404,"用户不存在！"),
    USERNAME_OR_PASSWORD_ERROR(400,"用户名或密码错误！"),
    GENERATE_TOKEN_ERROR(500,"用户凭证TOKEN生成失败！"),
    TOKEN_VERIFY_ERROR(401,"TOKEN校验失败！"),
    UN_AUTHORIZED(403,"登录用户未授权！"),
    CART_NOT_FOUND(404,"购物车为空！"),
    RECEIVER_NOT_FOUND(404,"收货人信息未查到！"),
    ORDER_NOT_FOUND(404,"订单未查到！"),
    ORDER_DETAIL_FOUND(404,"订单详情未查到！"),
    ORDER_STATUS_FOUND(404,"订单状态未查到！"),
    CREATE_ORDER_ERROR(500,"创建订单失败！"),
    CREATE_ORDER_DETAIL_ERROR(500,"创建订单详情失败！"),
    CREATE_ORDER_STATUS_ERROR(500,"创建订单详情失败！"),
    NO_STOCK_ERROR(500,"库存不足！"),
    WX_PAY_ORDER_FAIL(500,"下单失败"),
    INVALID_SIGN_ERROR(400,"无效的签名"),
    INVALID_ORDER_PARAM(400,"订单参数异常"),
    WXPAY_NOT_EQUAL_ORDERPAY(400,"微信支付金额和订单实付金额不一致！"),
    UPDATE_ORDER_STATUS_ERROR(500,"更新订单状态失败"),
    GET_PAY_URL_ERROR(500,"获取支付连接失败！"),
    ORDER_STATUS_ERROR(500,"订单状态异常！"),
    ;
    private int code;   //异常状态码

    private String msg; //异常信息
}
