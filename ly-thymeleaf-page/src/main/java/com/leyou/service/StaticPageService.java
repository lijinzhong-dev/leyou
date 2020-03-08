package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.utils.ThreadUtils;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/14
 * @Description: com.leyou.service
 * @version: 1.0
 */
@Slf4j
@Service
public class StaticPageService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    /**
     * 查询模型数据
     * @param spuId
     * @return
     */
    public Map<String,Object> loadModel(Long spuId) {
        Map<String,Object> model =new HashMap<>();

        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        //获取spuDetail
        SpuDetail detail = spu.getSpuDetail();

        //获取skus
        List<Sku> skus = spu.getSkus();

        //查询brand
        Brand brand = brandClient.queryBrandByBid(spu.getBrandId());

        //查询category 通过3级分类查询所有的分类（1到3级分类）
        List<Category> categories = categoryClient.queryCategoryByCid3(spu.getCid3());

        //查询specs
        List<SpecGroup> specs = specificationClient.querySpecGroupByCid(spu.getCid3());


        //其中的key要和页面进行渲染的数据名称一致，如<li th:each="c : ${categories}">
        model.put("spu",spu);
        model.put("detail",detail);
        model.put("skus",skus);
        model.put("brand",brand);
        model.put("categories",categories);
        model.put("specs",specs);
        return model;
    }


    @Autowired
    private TemplateEngine templateEngine; //thymeleaf模板引擎

    /**
     * 根据商品spuid生成其相应的html静态文件
     * @param spuid
     */
    public  void createHtml(Long spuid){
        //上下文  用来保存数据模型
        Context context = new Context();
        context.setVariables(loadModel(spuid));//设置数据

        //输出流，指定生成的静态文件放到哪里
        //本地测试 生成的文件放到E:\imags下,文件名称格式：商品supid.html
        File destination = new File("E:\\imags", spuid + ".html");

        //文件存在先删除
        if(destination.exists()){
            destination.delete();
        }

        try (PrintWriter writer = new PrintWriter(destination,"UTF-8")) {
            templateEngine.process("item",context,writer);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[静态化页面生成异常！]" +e);
        }//try(.....)中的流会自动释放,这是jdk1.8新功能

    }

    /**
     * 新建线程异步处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {

        ThreadUtils.execute(()->createHtml(spuId));

        System.out.println("异步线程生成商品:"+spuId);

        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });
    }

    /**
     * 处理MQ消息，生成新的html
     * @param spuId
     */
    public void createOrUpdateHtml(Long spuId) {
        this.createHtml(spuId);
    }

    /**
     * 处理MQ消息，根据spuid删除静态页
     * @param spuId
     */
    public void deleteHtmlById(Long spuId) {
        File file = new File("E:\\imags", spuId + ".html");
        file.delete();
    }
}
