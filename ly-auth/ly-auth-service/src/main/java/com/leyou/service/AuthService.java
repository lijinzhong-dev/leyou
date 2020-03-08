package com.leyou.service;

import com.leyou.client.UserClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.config.JwtProperties;
import com.leyou.entity.UserInfo;
import com.leyou.user.pojo.User;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/19
 * @Description: com.leyou.service
 * @version: 1.0
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录授权
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {

            //校验用户名密码
            User user = userClient.queryUserByUserNameAndPassword(username, password);
            if (user == null) {
                throw new LyExcception(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
            }
          try {
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username),
                    jwtProperties.getPrivateKey(),
                    jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            throw new LyExcception(ExceptionEnum.GENERATE_TOKEN_ERROR);
        }
    }

    /**
     * 用户的校验: 通过cookie获取token，然后校验token,校验通过后，返回用户信息。
     * @param token
     * @return
     */
    public Map<String,Object> verifyUserFromToken(String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            if(userInfo == null){
                throw new LyExcception(ExceptionEnum.TOKEN_VERIFY_ERROR);
            }

            Map<String,Object> map =new HashMap<>();

            // 解析成功要重新刷新token，也就是要重新生成cookie
            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            map.put("token", token);
            map.put("userInfo", userInfo);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyExcception(ExceptionEnum.TOKEN_VERIFY_ERROR);
        }
    }
}
