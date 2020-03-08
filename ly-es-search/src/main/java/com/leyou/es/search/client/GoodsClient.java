package com.leyou.es.search.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/9
 * @Description:   继承的接口GoodsApi是远程商品微服务暴露的接口
 * @version: 1.0
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi{

}
