package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.utils.NumberUtils;
import com.leyou.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/17
 * @Description: com.leyou.service
 * @version: 1.0
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    private static  final  Integer USER_NAME = 1;
    private static  final  Integer PHONE_NUMBER = 2;

    private static  final  Integer VALID_CODE_TIME_OUT = 5; //验证码有效时长

    //设置存入redis中的key的前缀，主要是为了能够方便阅读知道该key存储的是什么
    private static  final  String KEY_PREFIX="user.phone:verify:code";//存入redis中的key的前缀



    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 检验数据 主要包括对：手机号码、用户名的唯一性校验。
     * @param data  要校验的数据
     * @param type  要校验的数据类型：1：用户名；2：手机号码；
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        int count = 1;
        User user = new User();
        //判断数据类型
        if(type == USER_NAME){
            user.setUsername(data);
            count = userMapper.selectCount(user);
        }else if (type == PHONE_NUMBER){
            user.setPhone(data);
            count = userMapper.selectCount(user);
        }else {
            throw new LyExcception(ExceptionEnum.INVALID_USER_DATA_TYPE_ERROR);
        }

        /**
         * count等于0表示数据不在数据中存在，注册的数据可用，返回true
         */
        return count == 0 ;
    }

    /**  正常走阿里短信发送短息的方法
     *  根据前端输入的手机号码，发送验证码给该手机
     * @param phoneNum
     * @return
     */
    public void sendCodeToPhone(String phoneNum) {

        //随机生成6位验证码，
        String code = NumberUtils.generateCode(6);

        //包装要发送的消息 包括手机号、手机验证码
        Map<String,String> msg =new HashMap();
        msg.put("phone", phoneNum);
        msg.put("code", code);

        //通过amqpTemplate向MQ的交换机发送验证码
        amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code",msg);

        //将验证码保存到redis中，并指定验证码在5分钟内有效
        String key = KEY_PREFIX + phoneNum ;
        redisTemplate.opsForValue().set(key, code,VALID_CODE_TIME_OUT, TimeUnit.MINUTES);
    }

    /** 假的模拟发送手机验证码方法
     *  因为本人没有开通阿里短信服务，故不能发送短信，只模拟生成固定的验证码：88888
     *  用户再前端填写验证码时只要填写：888888 即可
     * @param phoneNum
     * @return
     */
    public void sendCodeToPhone_Test(String phoneNum) {

        //写死的验证码，
        String code = "888888";

        //将验证码保存到redis中，并指定验证码在5分钟内有效
        String key = KEY_PREFIX + phoneNum ;
        redisTemplate.opsForValue().set(key, code,1, TimeUnit.MINUTES);

    }

    /**
     *  用户注册
     * @param user  接收前端用户的注册信息（用户名、密码、电话号码）
     * @param code  接收前端输入的手机验证码
     * @return
     */
    public void register(User user, String code) {

        //从redis中获取指定手机号发送的验证码
        String redisCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if(StringUtils.isNotBlank(redisCode)){
            if(code.equals(redisCode)){

                //生成盐
                String salt = CodecUtils.generateSalt();
                user.setSalt(salt);//存储盐到数据库,当用户登录时需要得到该盐
                /**
                 * MD5加密并加盐
                 * CodecUtils.md5Hex(arg1, arg2)
                 * arg1：要进行加密的数据
                 * arg2：加密盐
                 */
                String md5Pwd = CodecUtils.md5Hex(user.getPassword(), salt);

                user.setPassword(md5Pwd);//设置加密后的密码

                user.setCreated(new Date());//设置注册时间
                //将数据插入数据库
                userMapper.insertSelective(user);
            }else {
                throw new LyExcception(ExceptionEnum.PHONE_CODE_ERROR);
            }
        }else {
            throw new LyExcception(ExceptionEnum.PHONE_CODE_EXPIRE);
        }
    }

    /**
     *  根据前端输入的用户名和密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    public User queryUserByUserNameAndPassword(String username, String password) {
        //根据用户名(数据库已对用户名建立索引)查询该用户的salt（加盐）
        User u = new User();
        u.setUsername(username);
        User user = userMapper.selectOne(u);

        if(user == null){
            throw  new LyExcception(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        /**
         * 根据输入的明文密码和该用户在数据库中的存储的加盐字段值，
         * 通过工具类CodecUtils.md5Hex(...)生成加密密码，
         * 用于匹配和数据库中的密文密码是否一致
         */
        String md5Pwd = CodecUtils.md5Hex(password, user.getSalt());

        if(!md5Pwd.equals(user.getPassword())){
            throw  new LyExcception(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        return user;
    }
}
