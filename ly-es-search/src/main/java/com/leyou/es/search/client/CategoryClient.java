package com.leyou.es.search.client;

import com.leyou.item.api.CategoryApi;
import com.leyou.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description:
 * @version: 1.0
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {

}
