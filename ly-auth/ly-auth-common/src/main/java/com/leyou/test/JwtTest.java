package com.leyou.test;

import com.leyou.entity.UserInfo;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "E:\\imags\\rsa.pub";

    private static final String priKeyPath = "E:\\imags\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 根据密文，生存RSA公钥和私钥,并写入指定文件
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成token
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        //这个是篡改过的token，无法解析
        //String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJOYW1lIjoiamFjayIsImV4cCI6MTUyNzMzMDY5NX0.VpGNedy1z0aR262uAp2sM6xB4ljuxa4fzqyyBpZcGTBNLodIfuCNZkOjdlqf-km6TQPoz3epYf8cc_Rf9snsGdz4YPIwpm6X14JKU9jwL74q6zy61J8Nl9q7Zu3YnLibAvcnC_y9omiqKN8-iCi7-MvM-LwVS7y_Cx9eu0aaY8E";

        //这个是正常原始的token，可以解析
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3MTQ2MjM4OX0.CbeCSWzFVY1byLo_Zwu5Uf77UErwStp6HgA3mnVSqgQ53Jxi7l7I1rRP3C_JYwO-E_2KMob-6apDNPGDao7ciu8uRhCyRhJSU7Z067vB5fBIa4rM4IYcqXlR0vcvEax_hQxGRDaINBO2o5YF6TwKH5kkTcKmHQcn7z9wt36IAW0";
        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}

