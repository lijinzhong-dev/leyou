package com.leyou.mq;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.leyou.config.SmsProperties;
import com.leyou.sendMsUtils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 编写消息监听器，当接收到消息后，我们发送短信
 */
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties prop;

    /**
     *  该方法只用来发送短信验证码
     * @param msg
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.sms.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.sms.exchange",
                    ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}))
    public void listenSms(Map<String, String> msg)  {
        if (msg == null || msg.size() <= 0) {
            // 放弃处理
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");

        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            // 放弃处理
            return;
        }

        try {
            // 发送消息
            SendSmsResponse resp = this.smsUtils.sendSms(phone, code,
                    prop.getSignName(),
                    prop.getVerifyCodeTemplate());
        }catch (Exception e){
            // 发送失败 直接抛出异常，会使消息再次回到RabbitMQ中成为Ready状态,等待消息再次被消费,避免消息丢失
            // 但也要注意 阿里短信有限流的控制，就是当短时间内多次发送短信，会被限流，禁止发送，所以一般发送短
            // 信出现异常时，最好别重试，记录异常即可，大不了，用户在没接收到验证码时，再次点击获取验证码
            e.printStackTrace();
            //throw new RuntimeException();

        }
    }
}