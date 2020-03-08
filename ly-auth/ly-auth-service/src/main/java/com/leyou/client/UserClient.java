package com.leyou.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/19
 * @Description: com.leyou.client
 * @version: 1.0
 */

@FeignClient("user-service")
public interface UserClient extends UserApi {
}
