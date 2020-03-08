package com.leyou.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WXPayConfiguration {

    /**
     * @ConfigurationProperties(prefix = "ly.pay")
     * 会将配置文件中以ly.pay为前缀的
     * 配置项的值一一对应地赋值给PayConfig类中的各个属性
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "ly.pay")
    public PayConfig payConfig() {
        return new PayConfig();
    }

    /**
     * 初始化WXPay
     * 第二个参数 WXPayConstants.SignType 是签名类型
     * @param payConfig
     * @return
     */
    @Bean
    public WXPay wxPay(PayConfig payConfig) {
        return new WXPay(payConfig, WXPayConstants.SignType.HMACSHA256);
    }
}