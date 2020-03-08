package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/26
 * @Description: com.leyou
 * @version: 1.0
 */
@EnableZuulProxy         // 开启zuul功能
@SpringCloudApplication  // 该注解包括下面三个
//@SpringBootApplication
//@EnableDiscoveryClient  //启动服务发现功能
//@EnableCircuitBreaker   //启动熔断功能
public class LyGateWay {
     public static void main(String[] args) {
         SpringApplication.run(LyGateWay.class,args);
      }
}
