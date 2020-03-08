package com.leyou.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description:   继承的接口GoodsApi是远程商品微服务暴露的接口
 * @version: 1.0
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi{

}
