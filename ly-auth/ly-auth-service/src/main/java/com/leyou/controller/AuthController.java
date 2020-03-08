package com.leyou.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.utils.CookieUtils;
import com.leyou.entity.UserInfo;
import com.leyou.service.AuthService;
import com.leyou.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/19
 * @Description: com.leyou.controller
 * @version: 1.0
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    //通过value注解从配置文件中获取ly.jwt.cookieName的值并赋值给cookieName
    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    /**
     * 登录授权
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response){
        //登录
        String token= authService.login(username,password);
        System.out.println("生成的token："+token);
        System.out.println("cookieName-----："+cookieName);
        //将token写入到cookie中 并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.newBuilder(response).httpOnly().request(request)
                                 .build(cookieName, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户的校验: 通过cookie获取token，然后校验token,校验通过后，返回用户信息。
     * @CookieValue注解主要是将请求的Cookie数据，映射到功能处理方法的参数上。
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN") String token,
                                               HttpServletRequest request,
                                               HttpServletResponse response){
        //当没有获取到浏览器中的token时，直接报错 返回403
        if (StringUtils.isBlank(token)) {
            throw new LyExcception(ExceptionEnum.UN_AUTHORIZED);
        }

        Map<String,Object> map= authService.verifyUserFromToken(token);

        //获取用户信息
        UserInfo userInfo = (UserInfo) map.get("userInfo");
        //拿到新生成的token
        String newToken = (String) map.get("token");

        //重新将token写入到cookie中 其实就是刷新token
        CookieUtils.newBuilder(response).httpOnly().request(request)
                .build(cookieName, token);

        return ResponseEntity.ok(userInfo);
    }
}
