package com.leyou.item.service;

import com.leyou.item.pojo.Item;
import org.springframework.stereotype.Service;
import java.util.Random;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description: com.leyou.item.service
 * @version: 1.0
 */
@Service
public class ItemService {

    /**
     * 测试案例
     */
    public Item saveItem(Item item){
        //随机生成一个0-100的随机数（但不包括100）
        int id = new Random().nextInt(100);
        item.setId(id);
        item.setName("oppo");
        return  item;
    }
}
