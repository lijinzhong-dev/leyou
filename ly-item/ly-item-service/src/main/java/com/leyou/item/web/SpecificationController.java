package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/2
 * @Description: com.leyou.item.web
 * @version: 1.0
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;
    /**
     * 根据商品分类cid查询其下的规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups = specificationService.querySpecGroupByCid(cid);
        return ResponseEntity.ok(specGroups);
    }


    /**
     * 根据商品分类cid查询其下的规格组及组内的规格参数
     * @param cid
     * @return
     */
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupAndSpecParamsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups=specificationService.querySpecGroupAndSpecParamsByCid(cid);
        return ResponseEntity.ok(specGroups);
    }


    /**
     * 新增规格分组
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public  ResponseEntity<Void> addSpecGroup(SpecGroup specGroup){
        specificationService.addSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * 修改规格分组
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public  ResponseEntity<Void> updateSpecGroup(SpecGroup specGroup){
        specificationService.updateSpecGroup(specGroup);
        return ResponseEntity.ok().build();
    }

    /**
     *  根据规格分组id删除
     * @param id
     * @return
     */
    @DeleteMapping("group/{id}")
    public  ResponseEntity<Void> delSpecGroupById(@PathVariable("id") Long id){
        specificationService.delSpecGroupById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询规格参数集合
     * @param gid 规格组id
     * @param cid 分类id
     * @param searching 是否可以搜索标识
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParamByList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching){
        List<SpecParam> specParams=specificationService.querySpecParamByList(gid,cid,searching);
        return ResponseEntity.ok(specParams);
    }



    /**
     *  修改规格参数
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateSpecParamByid(SpecParam specParam){
        specificationService.updateSpecParamByid(specParam);
        return ResponseEntity.ok(null);
    }
    /**
     *  新增规格参数
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> addSpecParam(SpecParam specParam){
        specificationService.addSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * 根据规格参数id删除规格参数
     * @param id
     * @return
     */
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> delSpecParam(@PathVariable("id") Long id){
        specificationService.delSpecParam(id);
        return ResponseEntity.ok(null);
    }
}
