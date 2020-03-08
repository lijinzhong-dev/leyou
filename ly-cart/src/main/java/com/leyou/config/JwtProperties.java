package com.leyou.config;

import com.leyou.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@Component
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;// 公钥地址

    private PublicKey publicKey; // 公钥

    private  String cookieName; //cookie名称

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    /**
     * @PostContruct：在构造方法执行之后执行该方法
     * 对象一旦实例化以后，就会读取公钥
     */
    @PostConstruct
    public void init(){
        try {
            // 获取公钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("获取公钥失败！", e);
            throw new RuntimeException();
        }
    }
    
    // getter setter ...
}