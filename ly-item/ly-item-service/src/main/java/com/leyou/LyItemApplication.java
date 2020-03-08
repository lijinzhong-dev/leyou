package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description: com.leyou
 * @version: 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient   //将服务注册到注册中心上(本项目使用eureka)
@MapperScan("com.leyou.item.mapper") //扫描mapper类所在的包路径 也可以扫描多个包 如@MapperScan("x.x.x","u.u.u")
public class LyItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyItemApplication.class,args);
    }
}
