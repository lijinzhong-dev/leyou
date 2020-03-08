package com.leyou.item.api;

import com.leyou.common.dto.CartDto;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 该类暴露接口供其他微服务调用
 */
public interface GoodsApi {

    /**
     *  分页查询spu
     * @param key         查询条件
     * @param saleable    是否上架
     * @param page        当前页码
     * @param rows        每页记录数
     * @return
     */
    @GetMapping("/spu/page")  //key=&saleable=true&page=1&rows=5
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );

    /**
     *  根据spuid查询spudetail
     *  @param spuid
     *  @return
     */
    @GetMapping("/spu/detail/{spuid}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("spuid") Long spuid);

    /**
     * 根据spuid查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 根据多个sku的id查询sku集合
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    List<Sku> querySkusBySkuIds(@RequestParam("ids") List<Long> ids);

    /**
     *  减库存
     * @param cartDtos
     * @return
     */
    @PostMapping("stock/decrease")
    void decreaseStock(@RequestBody List<CartDto> cartDtos);
}