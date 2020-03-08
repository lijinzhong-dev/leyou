package com.leyou.client;

import com.leyou.api.CartApi;
import com.leyou.interceptor.FeignHeaderIInterceptor;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.client
 * @version: 1.0
 */
@FeignClient(value = "cart-service"/*,configuration = FeignHeaderIInterceptor.class*/)
public interface CartClient  extends CartApi{
}
