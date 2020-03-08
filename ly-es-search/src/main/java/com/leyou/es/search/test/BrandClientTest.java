package com.leyou.es.search.test;

import com.leyou.es.search.client.BrandClient;
import com.leyou.es.search.client.CategoryClient;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description: com.leyou.item.test
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BrandClientTest {

    @Autowired
    private BrandClient brandClient;


    @Test
    public void test01(){

        Brand brand = brandClient.queryBrandByBid(1528L);

        //断言，第一个参数是期望值，第二个参数是实际值，当实际值和期望值不一致时，报错
        Assert.assertEquals(1,1);

        System.out.println(brand);
    }
}
