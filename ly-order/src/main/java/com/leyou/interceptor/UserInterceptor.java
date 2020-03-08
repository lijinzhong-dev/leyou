package com.leyou.interceptor;

import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import com.leyou.entity.UserInfo;
import com.leyou.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/21
 * @Description:   校验用户登录的拦截器
 * @version: 1.0
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private  JwtProperties jwtProperties;

    // 定义一个线程域，存放登录用户
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public UserInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties=jwtProperties;
    }

    /**
     *  在调用controller之前，在该方法里处理相关功能
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        //用公钥解析token获取其中用户信息
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            //传递用户信息(controller中会使用到)
            //request.setAttribute("user", info);
            tl.set(info);

            return  true; //用户登录放行
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[订单微服务],解析用户身份失败,"+e);
            return false;//用户未登录不放行！
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //用完数据，要清空
        tl.remove();
    }
    //获取线程域中的数据
    public static UserInfo getLoginUser() {
        return tl.get();
    }
}
