package com.leyou.es.search.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description: com.leyou.es.search.client
 * @version: 1.0
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
