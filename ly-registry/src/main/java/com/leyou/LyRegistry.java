package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/26
 * @Description: com.leyou
 * @version: 1.0
 */
@SpringBootApplication
@EnableEurekaServer //开启Eureka服务
public class LyRegistry {
     public static void main(String[] args) {
         SpringApplication.run(LyRegistry.class,args);
      }
}
