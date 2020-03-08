package com.leyou.test;

import com.leyou.mq.SmsListener;
import com.leyou.sendMsUtils.SmsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/17
 * @Description: com.leyou.test
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSendMessage {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public  void testSendMessage(){
        HashMap<Object, Object> msg = new HashMap<>();

        msg.put("phone","15811236037");//发送短信到的目标手机号
        msg.put("code","88888");       //发送的短信验证码

        amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code",msg);
    }


}
