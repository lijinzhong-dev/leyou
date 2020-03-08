package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.utils.JsonUtils;
import com.leyou.entity.UserInfo;
import com.leyou.interceptor.UserInterceptor;
import com.leyou.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/21
 * @Description: com.leyou.service
 * @version: 1.0
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //购物车用户id前缀
    private static  final  String KEY_PREFIX="cart:user:id:";
    /**
     * 添加购物车数据
     * @param cart
     * @return
     */
    public void addCart(Cart cart) {
        //获取登录用户的id
        UserInfo userInfo = UserInterceptor.getLoginUser();
        Long userId = userInfo.getId();

        //redis中key
        String key=KEY_PREFIX+userId;

        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        //根据商品skuid判断商品是否已经存在
        Boolean hasKey = operation.hasKey(cart.getSkuId().toString());
        if(hasKey){//存在 只修改数量即可
            //从redis中取出json格式的cart
            String json = (String) operation.get(cart.getSkuId().toString());
            //转成cart对象
            Cart oldCart = JsonUtils.toBean(json, Cart.class);
            Integer num = oldCart.getNum();
            oldCart.setNum(num + cart.getNum());//购物车中同一个商品的数量 = 之前数量 + 新加入的数量
            //写入到redis中
            operation.put(cart.getSkuId().toString(), JsonUtils.toString(oldCart));
        }else {//不存在 则新增该商品
            operation.put(cart.getSkuId().toString(),JsonUtils.toString(cart));;
        }
    }
    /**
     * 根据登录的用户id查询购物车中的商品
     * @return
     */
    public List<Cart> queryCartList() {

        return queyCartInLoginStatus();
    }

    /**
     * 从redis中查询购物车数据
     * @return
     */
    private List<Cart> queyCartInLoginStatus() {
        //获取登录用户的id
        UserInfo userInfo = UserInterceptor.getLoginUser();
        Long userId = userInfo.getId();
        //redis中key
        String key=KEY_PREFIX+userId;
        //判断该用户下的购物车是否为空
        if(!redisTemplate.hasKey(key)){
            throw new LyExcception(ExceptionEnum.CART_NOT_FOUND);
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);

        List<Object> values = operations.values();

        //存储cart的集合
        List<Cart> carts = new ArrayList<>();

        for (Object value : values) {
            String val = value.toString();
            Cart cart = JsonUtils.toBean(val, Cart.class);
            carts.add(cart);
        }
        return carts;
    }


    /**
     * 根据skuid增加或减少购物车中的商品数量
     * @param skuId
     * @param num
     * @return
     */
    public void incrOrDecrSku(Long skuId, Integer num) {
        //获取登录用户的id
        UserInfo userInfo = UserInterceptor.getLoginUser();
        Long userId = userInfo.getId();
        //redis中key
        String key=KEY_PREFIX+userId;

        BoundHashOperations<String, Object, Object> option = redisTemplate.boundHashOps(key);
        //获取redis中json格式的商品信息
        String  json = (String) option.get(skuId.toString());
        Cart cart = JsonUtils.toBean(json, Cart.class);
        //修改购物车中商品的数量
        cart.setNum(num);

        //修改后重新写入redis中
        option.put(skuId.toString(), JsonUtils.toString(cart));
    }

    /**
     *  根据skuid删除购物车中的商品
     * @param skuId
     * @return
     */
    public void deleteCartBySkuId(String skuId) {
        //获取登录用户的id
        UserInfo userInfo = UserInterceptor.getLoginUser();
        Long userId = userInfo.getId();
        //redis中key
        String key=KEY_PREFIX+userId;
        BoundHashOperations<String, Object, Object> option = redisTemplate.boundHashOps(key);
        //删除
        option.delete(skuId);
    }
}
