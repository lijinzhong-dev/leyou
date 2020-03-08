package com.leyou.item.web;

import com.leyou.common.dto.CartDto;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/4
 * @Description: com.leyou.item.web
 * @version: 1.0
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    /**
     *  分页查询spu
     * @param key         查询条件
     * @param saleable    是否上架
     * @param page        当前页码
     * @param rows        每页记录数
     * @return
     */
    @GetMapping("/spu/page")  //key=&saleable=true&page=1&rows=5
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){

        PageResult<Spu> spus=goodsService.querySpuByPage(key,saleable,page,rows);
        return ResponseEntity.ok(spus);

    }

    /**
     * 新增商品
     * @param spu 通过注解@RequestBody来接收前端传入的json格式的数据参数
     * @return
     */
    @PostMapping("goods")
    public  ResponseEntity<Void> addSpu(@RequestBody Spu spu){
        goodsService.addSpu(spu);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     *  根据spuid查询spudetail
     * @param spuid
     * @return
     */
    @GetMapping("/spu/detail/{spuid}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuid") Long spuid){
        SpuDetail spuDetail= goodsService.querySpuDetailBySpuId(spuid);
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuid查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    public  ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        List<Sku> skus=goodsService.querySkuBySpuId(id);
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据多个sku的id查询sku集合
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public  ResponseEntity<List<Sku>> querySkusBySkuIds(@RequestParam("ids") List<Long> ids){
        List<Sku> skus=goodsService.querySkusBySkuIds(ids);
        return ResponseEntity.ok(skus);
    }

    /**
     *  修改商品
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public  ResponseEntity<Void> updateSpu(@RequestBody Spu spu){
        goodsService.updateSpu(spu);
        return ResponseEntity.ok().body(null);
    }

    /**
     * 根据spuid修改上下架
     * @param spuid
     * @param saleable
     * @return
     */
    @PutMapping("salestatus/{spuid}/{saleable}")
    public ResponseEntity<Void> updateSaleable(
            @PathVariable("spuid") Long spuid,
            @PathVariable("saleable") Boolean saleable){
        goodsService.updateSaleable(spuid,saleable);
        return ResponseEntity.ok().body(null);
    }

    /**
     * 根据spuid删除商品
     * @param spuid
     * @return
     */
    @DeleteMapping("goods/{spuid}")
    public ResponseEntity<Void> delGoodsById(@PathVariable("spuid") Long spuid){
        goodsService.delGoodsById(spuid);
        return ResponseEntity.ok().body(null);
    }

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu=goodsService.querySpuById(id);
        return ResponseEntity.ok(spu);
    }

    /**
     *  减库存
     * @param cartDtos
     * @return
     */
    @PostMapping("stock/decrease")
    public  ResponseEntity<Void> decreaseStock(@RequestBody List<CartDto> cartDtos){
        goodsService.decreaseStock(cartDtos);
        return ResponseEntity.ok(null);
    }

}
