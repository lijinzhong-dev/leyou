package com.leyou.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description:
 * @version: 1.0
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {

}
