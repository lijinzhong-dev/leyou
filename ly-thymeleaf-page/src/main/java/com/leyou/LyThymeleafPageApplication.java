package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/13
 * @Description: com.leyou
 * @version: 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient //开启服务发现功能 本次使用的eureka
@EnableFeignClients  //开启feign功能
public class LyThymeleafPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyThymeleafPageApplication.class,args);
    }
}
