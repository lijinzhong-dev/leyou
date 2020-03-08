package com.leyou.interceptor;

import com.leyou.common.utils.CookieUtils;
import com.leyou.entity.UserInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.interceptor
 * @version: 1.0
 */
@Configuration
@Component
public class FeignHeaderIInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = requestAttributes.getRequest();

        //获取token
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        requestTemplate.header("LY_TOKEN",token);

    }
}
