package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/19
 * @Description: com.leyou.user.api
 * @version: 1.0
 */
/**
 * 该类暴露接口供其他微服务调用
 */
public interface UserApi {
    /**
     *  根据前端输入的用户名和密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    User queryUserByUserNameAndPassword(@RequestParam("username") String username,
                                        @RequestParam("password") String password);
}
