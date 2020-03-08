package com.leyou.controller;

import com.leyou.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/23
 * @Description:  接收微信调用的controller
 * @version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {

    @Autowired
    private OrderService orderService;
               //produces = "application/xml"指定返回结果是xml格式
    @PostMapping(value = "wxpay",produces = "application/xml")
        public  Map<String,String> hello(@RequestBody Map<String,String> result){

            /**
             *  处理微信的回调
             *  当该方法不发生异常就会返回如下的成功信息，
             *  如果有异常，下面成功的信息就不会发送给微信，微信会再次调用该方法
             */
            orderService.handlerNotify(result);

            log.info("[微信回调] 接收微信支付回调，结果：{}",result);
            //返回给微信的成功消息
            Map<String,String> msg=new HashMap<>();
            msg.put("return_code", "SUCCESS");
            msg.put("return_msg", "OK");
            return msg;
    }
}
