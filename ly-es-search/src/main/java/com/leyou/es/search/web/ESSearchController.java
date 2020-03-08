package com.leyou.es.search.web;

import com.leyou.common.vo.PageResult;
import com.leyou.es.search.pojo.Goods;
import com.leyou.es.search.pojo.SearchRequest;
import com.leyou.es.search.service.ESSearchService;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/10
 * @Description: com.leyou.es.search.web
 * @version: 1.0
 */
@RestController
public class ESSearchController {
    @Autowired
    private ESSearchService esSearchService;

    /**
     *  分页从es索引库中查询商品信息
     * @param searchRequest
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> queryByPage(@RequestBody SearchRequest searchRequest, HttpServletRequest request){

        PageResult<Goods> result=esSearchService.queryByPage(searchRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据分类级别3，即cid3，查询出1-3级别的分类用list集合包装
     * @param cid3
     * @return
     */
    @GetMapping("allCategoryLevel")
    public ResponseEntity<List<Category>> queryCategoryByCid3(@RequestParam("cid3") Long cid3) {
        List<Category> categories = esSearchService.queryCategoryByCid3(cid3);
        return ResponseEntity.ok(categories);
    }
}
