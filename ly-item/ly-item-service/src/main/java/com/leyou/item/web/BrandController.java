package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/27
 * @Description: com.leyou.item.web
 * @version: 1.0
 */
@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     *  分页查询品牌信息
     * @param page  当前页号
     * @param rows  每一页数据记录条数
     * @param sortBy 按某个字段排序
     * @param desc   是否按降序进行排序
     * @param q      搜索条件
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "q",required = false) String q

    ){
        PageResult<Brand> result=brandService.queryBrandsByPage(page,rows,sortBy,desc,q);
        return  ResponseEntity.ok(result);
    }

    /**
     *  新增品牌
     * @param brand  接收品牌名称、品牌首字母、品牌logo
     * @param cids 接收品牌所属的商品分类，前端是数组类型,后台用List接收
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addBrand(Brand brand,@RequestParam("cids") List<Long> cids){

        brandService.addBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据品牌id删除品牌
     * @param bid
     * @return
     */
    @DeleteMapping("{bid}")
    public ResponseEntity<Void> delBrandById(@PathVariable("bid") Long bid){
        brandService.delBrandById(bid);
        return ResponseEntity.ok(null);
    }

    /**
     *  根据3级分类id查询品牌信息
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategoryId(@PathVariable("cid") Long cid){
        List<Brand> brands= brandService.queryBrandByCategoryId(cid);
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据品牌id查询品牌信息
     * @param bid
     * @return
     */
    @GetMapping("{bid}")
    public ResponseEntity<Brand> queryBrandByBid(@PathVariable("bid") Long bid){
        Brand brand=brandService.queryBrandById(bid);
        return ResponseEntity.ok(brand);
    }

    /**
     * 根据品牌id集合查询品牌信息
     * @param bids
     * @return
     */
    @GetMapping("byIdList")
    public ResponseEntity<List<Brand>> queryBrandByBidList(@RequestParam("bids") List<Long> bids){
        List<Brand> brands = brandService.queryBrandByIdList(bids);
        return ResponseEntity.ok(brands);
    }
}
