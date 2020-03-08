package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 该类暴露接口供其他微服务调用
 */
public interface SpecificationApi {
    /**
     * 查询规格参数集合
     * @param gid 规格组id
     * @param cid 分类id
     * @param searching 是否可以搜索标识
     * @return
     */
    @GetMapping("spec/params")
    List<SpecParam> querySpecParamByList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching);

    /**
     * 根据商品分类cid查询其下的规格组及组内的规格参数
     * @param cid
     * @return
     */
    @GetMapping("spec/{cid}")
    List<SpecGroup> querySpecGroupByCid(@PathVariable("cid") Long cid);

}
