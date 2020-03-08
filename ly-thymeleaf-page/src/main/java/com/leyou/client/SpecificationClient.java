package com.leyou.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description: com.leyou.es.search.client
 * @version: 1.0
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
