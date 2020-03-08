package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 该类暴露接口供其他微服务调用
 */
public interface CategoryApi {
    /**
     * 根据分类id集合查询商品分类信息
     * @param ids
     * @return
     */
    @GetMapping("category/list/ids")
    List<Category> queryCategoryByCids(@RequestParam("ids") List<Long> ids);

    /**
     * 根据分类级别3，即cid3，查询出1-3级别的分类用list集合包装
     * @param cid3
     * @return
     */
    @GetMapping("category/allCategoryLevel")
    List<Category> queryCategoryByCid3(@RequestParam("cid3") Long cid3);
}
