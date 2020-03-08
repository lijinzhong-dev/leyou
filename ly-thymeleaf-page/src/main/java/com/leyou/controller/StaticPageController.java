package com.leyou.controller;

import com.leyou.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/13
 * @Description: com.leyou.controller
 * @version: 1.0
 */
@Controller
public class StaticPageController {

    @Autowired
    private StaticPageService staticPageService;


    @GetMapping("item/{id}.html")
    public  String toSpuStaticPage(@PathVariable("id") Long spuId, Model model){
        System.out.println("要访问的商品supId:"+spuId);
        //查询模型数据
        Map<String,Object> attributes=staticPageService.loadModel(spuId);

        /**
         * 商品数据model
         * 可以通过 model.addAllAttributes（Map<String,Object>）
         * 整体对数据进行封装，其中Map中的key就是和页面对应着
         */
        model.addAllAttributes(attributes);

        // 异步创建静态化页面
        staticPageService.asyncExcute(spuId);


        //返回视图
        return "item";
    }
}
