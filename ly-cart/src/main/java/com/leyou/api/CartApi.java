package com.leyou.api;

import com.leyou.entity.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Auther: lijinzhong  ljznnnnnn
 * @Date: 2019/10/22
 * @Description: com.leyou.api
 * @version: 1.0
 */
public interface CartApi {
    /**
     *  根据用户id和skuid删除购物车中的商品
     * @param skuId
     * @return
     */
    @DeleteMapping("remote/{id}")
    void deleteCartBySkuIdAndUserId(@PathVariable("id") String skuId);
}
