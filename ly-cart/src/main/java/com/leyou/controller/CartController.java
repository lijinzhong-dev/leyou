package com.leyou.controller;

import com.leyou.entity.UserInfo;
import com.leyou.pojo.Cart;
import com.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/21
 * @Description: com.leyou.controller
 * @version: 1.0
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车数据
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return  ResponseEntity.ok(null);
    }

    /**
     * 根据登录的用户id查询购物车中的商品
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList(){
        List<Cart> carts=cartService.queryCartList();
        return ResponseEntity.ok(carts);
    }

    /**
     * 根据skuid增加或减少购物车中的商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> incrOrDecrSku(@RequestParam("id") Long skuId,
                                           @RequestParam("num") Integer num){
        cartService.incrOrDecrSku(skuId,num);
        return  ResponseEntity.ok(null);
    }

    /**
     *  根据skuid删除购物车中的商品
     * @param skuId
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCartBySkuId(@PathVariable("id") String skuId){
        cartService.deleteCartBySkuId(skuId);
        return ResponseEntity.ok(null);
    }

    /**
     *  根据用户id和skuid删除购物车中的商品
     * @param skuId
     * @return
     */
    @DeleteMapping("remote/{id}")
    public ResponseEntity<Void> deleteCartBySkuIdAndUserId(@PathVariable("id") String skuId){
        System.out.println("^^^^^^^^^^^^^^^^^^^^-----");
        cartService.deleteCartBySkuId(skuId);
        return ResponseEntity.ok(null);
    }
}
