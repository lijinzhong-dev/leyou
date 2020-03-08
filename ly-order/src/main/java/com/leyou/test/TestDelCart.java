package com.leyou.test;

import com.leyou.controller.CartController;
import com.leyou.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: 测试删除购物车的中商品
 * @version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDelCart {
    @Autowired
    private CartController cartController;

    @Test
    public  void delCart(){

        //cartController.deleteCartBySkuIdAndUserId("13134026855",userInfo);
    }
}
