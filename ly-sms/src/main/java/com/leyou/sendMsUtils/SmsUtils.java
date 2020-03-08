package com.leyou.sendMsUtils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.leyou.config.SmsProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 发送手机短信的工具类
 */
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties prop;

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    static final Logger logger = LoggerFactory.getLogger(SmsUtils.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    //设置存入redis中的key的前缀，主要是为了能够方便阅读知道该key存储的是什么
    private static  final  String KEY_PREFIX="sms.phoneNum";

    //同一个手机号两次发送短信的时间间隔必须不能短于1分钟
    private static  final  Long SMS_MIN_INTERVAL_IN_MILLIS=60000L; //单位毫秒

    public SendSmsResponse sendSms(String phone, String code, String signName, String template) throws ClientException, ClientException {

        String key= KEY_PREFIX + phone;
        /**
         * 针对某一个手机号码，不允许在指定的时间内多次发送短息
         * 主要是为了避免触发阿里短信的限流机制
         */
        //从redis中获取短信发送成功后的当时时间
        String lastTime = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(lastTime)){
            Long last = Long.valueOf(lastTime);//上一次发送短信成功时的当时时间

            if(System.currentTimeMillis() - last < SMS_MIN_INTERVAL_IN_MILLIS){
                return  null;
            }
        }


        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                prop.getAccessKeyId(), prop.getAccessKeySecret());
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(template);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("123456");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        logger.info("发送短信状态：{}", sendSmsResponse.getCode());
        logger.info("发送短信消息：{}", sendSmsResponse.getMessage());

        //短信发送成功后，在redis中记录当时的时间,并指定该时间的生存时长为1分钟，过期作废
        redisTemplate.opsForValue()
                .set(key, String.valueOf(System.currentTimeMillis()),1, TimeUnit.MINUTES);

        return sendSmsResponse;
    }
}