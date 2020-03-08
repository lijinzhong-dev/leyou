package com.leyou.filters;

import com.leyou.common.utils.CookieUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.leyou.entity.UserInfo;
import com.leyou.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 获取cookie中的token,通过JWT对token进行校验
 * 通过：则放行；不通过：则重定向到登录页
 */
@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class AuthFilter extends ZuulFilter{

    @Value("ly.jwt.cookieName")
    private  String cookieName;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE; //前置过滤器
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;//过滤器顺序(最好在某些官方过滤器之前)
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();

        //获取request
        HttpServletRequest request = ctx.getRequest();

        //获取请求的url路径
        String requestPath = request.getRequestURI();

        //判断是否可以放行
        Boolean allowPath = isAllowPath(requestPath);


        return !allowPath; //是否要进行拦截 ，true是拦截,false是放行
    }

    //过滤逻辑
    @Override
    public Object run() throws ZuulException {
        //获取上下文，为了从中获取request
        RequestContext ctx = RequestContext.getCurrentContext();

        //获取request
        HttpServletRequest request = ctx.getRequest();



        //解析token
        try {
            //从自定义的工具类中获取cookie中的token
            String token = CookieUtils.getCookieValue(request, cookieName);

            //用公钥解析token获取其中用户信息
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            //TODO 用户权限校验（就是用户所属角色,能够访问哪些接口）

        } catch (Exception e) {
            //解析token失败，就是未登录，那么就要进行拦截
            ctx.setSendZuulResponse(false); //false要进行拦截，true是放行

            //返回状态码
            ctx.setResponseStatusCode(403);

        }
        return null;
    }

    /**
     *  判断访问路径是否在白名单中
     * @param path
     * @return
     */
    private Boolean isAllowPath(String path){
        List<String> allowPaths = filterProperties.getAllowPaths();
        boolean flag=false;
        for (String allowPath : allowPaths) {
            //判断访问路径是否以白名单中指定的url开头
            if (path.startsWith(allowPath)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
