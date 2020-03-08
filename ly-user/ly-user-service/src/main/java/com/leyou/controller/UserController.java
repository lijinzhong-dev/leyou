package com.leyou.controller;

import com.leyou.service.UserService;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/17
 * @Description: com.leyou.controller
 * @version: 1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 检验数据是否可用 主要包括对：手机号、用户名的唯一性校验。
     * @param data  要校验的数据
     * @param type  要校验的数据类型：1：用户名；2：手机号码；
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(
                                          @PathVariable("data") String data,
                                          @PathVariable("type") Integer type){
        Boolean flag=userService.checkData(data,type);

        return ResponseEntity.ok(flag);
    }

    /**
     *  根据前端输入的手机号码，发送验证码给该手机
     * @param phoneNum
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCodeToPhone(
               @RequestParam("phoneNum") String phoneNum){
        //走阿里短信发送验证码
        //userService.sendCodeToPhone(phoneNum);

        //模拟发送验证码，其实验证码写死成：888888
        userService.sendCodeToPhone_Test(phoneNum);

        //当发送成功时，返回204 (HttpStatus.NO_CONTENT)
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     *  用户注册
     * @param user  接收前端用户的注册信息（用户名、密码、电话号码）
     * @param code  接收前端输入的手机验证码
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user,
                                         BindingResult result,
                                         @RequestParam("code") String code){
        //当有字段错误提示时
        if(result.hasFieldErrors()){
            //其中e.getDefaultMessage()获取的提示信息就是hibernate-validator在bean上定义的message
            //Collectors.joining("|")将各个错误提示信息用 | 分隔
            String errorMsg = result.getFieldErrors().stream().map(e -> e.getDefaultMessage())
                                           .collect(Collectors.joining("|"));

            throw  new RuntimeException(errorMsg);
        }


        userService.register(user, code);
        //注册成功返回状态码 201
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *  根据前端输入的用户名和密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUserByUserNameAndPassword(@RequestParam("username") String username,
                          @RequestParam("password") String password){
        User user =  userService.queryUserByUserNameAndPassword(username,password);
        return ResponseEntity.ok(user);
    }

}
