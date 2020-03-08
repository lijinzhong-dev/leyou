package com.leyou.item.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.item.pojo.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description: com.leyou.item.web
 * @version: 1.0
 */
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 使用Rest风格处理异常的测试案例：
     */
    @PostMapping //新增用POST请求
    public ResponseEntity<Item> saveItem(Item item){
        if(item.getPrice()==null){
            throw  new LyExcception(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        Item saveItem = itemService.saveItem(item);
        /*
         * rest风格:
         * ResponseEntity.status(HttpStatus.CREATED)  新增成功状态码是201
         * body(saveItem) 是响应体 其中的参数是返回的结果
         */
        return  ResponseEntity.status(HttpStatus.CREATED).body(saveItem);
    }

    /**
     *  原始处理异常的案例1：
     */
    @PostMapping("common") //新增用POST请求
    public Item commonSaveItem(Item item){
        if(item.getPrice()==null){
          throw new RuntimeException("价格不能为空!");
        }
        Item saveItem = itemService.saveItem(item);
        return  saveItem;
    }
    /**
     *  原始处理异常的案例2：
     *  ResponseEntity<Item>:是响应实体，其中的泛型Item是具体的响应实体内容。
     */
    @PostMapping("common2") //新增用POST请求
    public ResponseEntity<Item> common2SaveItem(Item item){
        if(item.getPrice()==null){
            //新增时,参数有误,设置响应实体的状态码为400和响应体null
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Item saveItem = itemService.saveItem(item);
                //新增成功时,设置响应实体的状态码为201和响应体saveItem
        return  ResponseEntity.status(HttpStatus.CREATED).body(saveItem);
    }
}
