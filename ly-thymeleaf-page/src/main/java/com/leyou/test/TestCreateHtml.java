package com.leyou.test;

import com.leyou.service.StaticPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/14
 * @Description: com.leyou.test
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCreateHtml {

    @Autowired
    private StaticPageService staticPageService;
    /**
     * 测试生成商品详情静态页html
     */
    @Test
    public void testCreateHtml(){

        long supId=125L;

        staticPageService.createHtml(supId);
    }
}
