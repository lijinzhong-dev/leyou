package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/30
 * @Description: 根据父节点id查询商品分类
 * @version: 1.0
 */
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * @param pid
     * @return  ResponseEntity<List<Category>>  使用的是rest风格
     * @RequestParam("pid") 表示参数pid必须要输入,否则报错
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam("pid") Long pid){


        //根据pid查询商品分类信息
        List<Category> categories = categoryService.queryCategoryByPid(pid);

        //ResponseEntity.status(HttpStatus.OK).body(categories);简写方式如下面的return返回值

        return ResponseEntity.ok(categories);
    }

    /**
     * 新增商品分类节点
     */
    @PostMapping
    public ResponseEntity<Void> addCategoryId(Category category){
        System.out.println("新增分类节点："+category);
        categoryService.addCategoryId(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
    /**
     * 修改商品分类节点
     */
    @PutMapping
    public ResponseEntity<Void> updateCategoryId(Category category){
        categoryService.updateCategoryId(category);
        return ResponseEntity.ok().build();
    }
    /**
     * 根据id删除商品分类
     */
    @DeleteMapping
    public ResponseEntity<Void> delCategoryId(Long id){
        categoryService.delCategoryId(id);
        return ResponseEntity.ok().build();

    }

    /**
     *  根据品牌id集合查询商品名称
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCatogeriesByBid(@PathVariable("bid") Long bid){
        List<Category> categories= categoryService.queryCatogeriesByBid(bid);
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据分类id集合查询商品分类信息
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByCids(@RequestParam("ids") List<Long> ids){
        List<Category> catogories=categoryService.queryByCids(ids);
        return ResponseEntity.ok(catogories);
    }

    /**
     * 根据分类级别3，即cid3，查询出1-3级别的分类用list集合包装
     * @param cid3
     * @return
     */
    @GetMapping("allCategoryLevel")
    public ResponseEntity<List<Category>> queryCategoryByCid3(@RequestParam("cid3") Long cid3){
        List<Category> categories = categoryService.queryCategoryByCid3(cid3);
        return ResponseEntity.ok(categories);
    }
}
