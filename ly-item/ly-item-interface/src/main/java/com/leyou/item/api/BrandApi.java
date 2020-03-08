package com.leyou.item.api;


import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 该类暴露接口供其他微服务调用
 */
public interface BrandApi {
    /**
     * 根据品牌id品牌信息
     * @param bid
     * @return
     */
    @GetMapping("brand/{bid}")
    Brand queryBrandByBid(@PathVariable("bid") Long bid);

    /**
     * 根据品牌id集合查询品牌信息
     * @param bids
     * @return
     */
    @GetMapping("brand/byIdList")
    List<Brand> queryBrandByBidList(@RequestParam("bids") List<Long> bids);
}
