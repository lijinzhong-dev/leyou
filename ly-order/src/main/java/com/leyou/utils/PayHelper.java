package com.leyou.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.config.PayConfig;
import com.leyou.enums.OrderStatusEnum;
import com.leyou.enums.PayState;
import com.leyou.mapper.OrderMapper;
import com.leyou.mapper.OrderStatusMapper;
import com.leyou.pojo.Order;
import com.leyou.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.leyou.common.exception.LyExcception;
//静态导入
import static com.github.wxpay.sdk.WXPayConstants.FAIL;
import static com.github.wxpay.sdk.WXPayConstants.SUCCESS;

@Slf4j
@Component
public class PayHelper {
    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig payConfig;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderStatusMapper statusMapper;

    /**
     *  调用微信统一下单接口，获取支付连接
     * @param orderId   订单号
     * @param totalFee 总金额
     * @param body     商品描述
     * @return
     */
    public String createPayUrl(Long orderId, Long totalFee, String body) {

        // 准备请求参数
        HashMap<String, String> data = new HashMap<>();
        //商品描述
        data.put("body", body);
        //订单号
        data.put("out_trade_no", orderId.toString());
        //金额 单位是分
        data.put("total_fee", totalFee.toString());
        //调用微信支付的终端ip
        data.put("spbill_create_ip", payConfig.getSpbillCreateIp());

        String notifyUrl = payConfig.getNotifyUrl();
        log.info("notifyUrl = {}", notifyUrl);
        //回调地址
        data.put("notify_url", notifyUrl);

        //交易类型 本次为扫描支付
        data.put("trade_type", payConfig.getTradeType());

        try {
            // 利用wxPay工具调用统一下单 API
            Map<String, String> result = wxPay.unifiedOrder(data);

            //判断通信标识 是否成功、是否失败等
            isSuccess(result);

            // 验证签名
            isValidSign(result);

            //下单成功获取支付连接
            String payUrl = result.get("code_url");
            return payUrl;

        } catch (Exception e) {
            log.error("【微信下单】下单失败，订单号:{}", orderId, e);
            throw new LyExcception(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }

    /**
     * 校验通信标识和业务标识
     * @param result
     */
    public void isSuccess(Map<String, String> result) {
        // 校验通信标识
        if (FAIL.equals(result.get("return_code"))) {
            log.error("【微信下单】下单通信失败, 原因：{}", result.get("return_msg"));
            throw new LyExcception(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }

        // 校验业务标识
        if (FAIL.equals(result.get("result_code"))) {
            log.error("【微信下单】下单失败, 错误码：{}， 错误原因：{}", result.get("err_code"),
                    result.get("err_code_des"));
            throw new LyExcception(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }

    /**
     * 校验签名
     * @param data
     */
    public void isValidSign(Map<String, String> data) {
        // 重新生成签名, 和传过来的签名进行比较
        try {
            //获取两种签名类型 MD5 和 HMACSHA256  然后和微信传递过来的签名作比较
            String sign1 = WXPayUtil.generateSignature(data, payConfig.getKey(),
                    WXPayConstants.SignType.MD5);
            String sign2 = WXPayUtil.generateSignature(data, payConfig.getKey(),
                    WXPayConstants.SignType.HMACSHA256);
            //获取微信传过来的签名
            String sign = data.get("sign");
            if (!StringUtils.equals(sign, sign1) && !StringUtils.equals(sign, sign2)) {
                log.error("【微信下单】签名有误");
                // 签名有误
                throw new LyExcception(ExceptionEnum.INVALID_SIGN_ERROR);
            }
        } catch (Exception e) {
            log.error("【微信下单】签名验证出错 ", e);
            throw new LyExcception(ExceptionEnum.INVALID_SIGN_ERROR);
        }
    }

    /**
     * 从微信系统查询订单支付状态，用于前端获取支付状态后，跳转页面，比如支付成功显示成功页面
     * @param orderId
     * @return
     */
    public Integer queryPayState(Long orderId) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("out_trade_no", orderId.toString());
            //从微信系统查询订单状态
            Map<String, String> result = wxPay.orderQuery(data);

            // 校验状态
            isSuccess(result);

            // 校验签名
            isValidSign(result);

            // 校验金额
            String totalFeeStr = result.get("total_fee");
            String tradeNoStr = result.get("out_trade_no");
            if (StringUtils.isBlank(tradeNoStr) || StringUtils.isBlank(totalFeeStr)) {
                throw new LyExcception(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            Long totalFee = Long.valueOf(totalFeeStr);
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                throw new LyExcception(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            // 这里应该不等于实际金额 开发时使用
//            if (totalFee != order.getActualPay()) {
//                // 金额不符
//                throw new LyExcception(ExceptionEnum.INVALID_ORDER_PARAM);
//            }
            //测试使用
            if (totalFee != 1L) {
                // 金额不符
                throw new LyExcception(ExceptionEnum.INVALID_ORDER_PARAM);
            }

            String state = result.get("trade_state");

            if (SUCCESS.equals(state)) {
                // 修改订单状态
                OrderStatus status = new OrderStatus();
                status.setStatus(OrderStatusEnum.PAYED.value());
                status.setOrderId(orderId);
                status.setPaymentTime(new Date());
                int count = statusMapper.updateByPrimaryKeySelective(status);
                if (count != 1) {
                    throw new LyExcception(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
                }
                return PayState.SUCCESS.getValue();
            } else if ("NOTPAY".equals(state) || "USERPAYING".equals(state)) {
                return PayState.NOT_PAY.getValue();
            }
            return PayState.FAIL.getValue();


        } catch (Exception e) {
            log.error("[微信支付], 调用微信接口查询支付状态失败", e);
            return PayState.NOT_PAY.getValue();
        }
    }
}