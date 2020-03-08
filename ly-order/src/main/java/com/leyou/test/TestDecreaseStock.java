package com.leyou.test;

import com.leyou.client.GoodsClient;
import com.leyou.common.dto.CartDto;
import com.leyou.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: 测试减库存
 * @version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDecreaseStock {
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public  void decreaseStock(){
        List<CartDto> cartDtos = new ArrayList<>();
        CartDto cartDto = new CartDto();
        cartDto.setNum(1111);
        cartDto.setSkuId(2600242L);
        cartDtos.add(cartDto);
        goodsClient.decreaseStock(cartDtos);
    }
}
