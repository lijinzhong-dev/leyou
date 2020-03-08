package com.leyou.config;

import com.leyou.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/21
 * @Description: 配置SpringMVC，使过滤器生效
 * @version: 1.0
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private  JwtProperties jwtProperties;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器并指定拦截哪些请求
        //registry.addInterceptor(new UserInterceptor(jwtProperties))
                //.addPathPatterns("/**");
        registry.addInterceptor(new UserInterceptor(jwtProperties))
                .addPathPatterns("/**")
                .excludePathPatterns("/remote/**");
    }
}
