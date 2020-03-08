package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/27
 * @Description: com.leyou.item.service
 * @version: 1.0
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandsByPage(Integer page, Integer rows, String sortBy, Boolean desc, String q) {
        //分页助手会拦截要执行的查询sql，在其后添加此处设置的分页条件
        PageHelper.startPage(page,rows);//指定从哪页开始查,查多少条数据

        //创建Example对象用来封装查询条件，记得必须传入要查询表对应实体类的字节码,如Brand.class
        Example example = new Example(Brand.class);

        //根据前端页面搜索条件模糊查询
        if(StringUtils.isNotBlank(q)){
            /**
             * 查询条件类似于：
             * select * from tb_brand WHERE name like '%爱%' or letter='A'
             */
            example.createCriteria().orLike("name","%"+q+"%")
                   .orEqualTo("letter",q.toUpperCase());//注意：转成大写,因为数据库中letter是大写
        }

        //按指定字段排序
        if(StringUtils.isNotBlank(sortBy)){
            //根据指定的字段进行降序或升序排序
            String orderByClause= sortBy + (desc ? " DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }

        //执行查询 注意：其实此处的返回值List<Brand>是一个Page<T>
        List<Brand> brands = brandMapper.selectByExample(example);

        //没查到抛出异常
        if(CollectionUtils.isEmpty(brands)){
            throw new LyExcception(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //解析分页结果List<Brand>，实质就是Page<T>
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        //获取数据总条数
        long total = pageInfo.getTotal();

        //获取总页数
        long totalPages = pageInfo.getPages();

        return  new PageResult<Brand>(total,totalPages,brands);
    }

    /**
     *  新增品牌
     * @param brand
     * @param categories
     */
    @Transactional //操作多个数据库开启事务
    public void addBrand(Brand brand, List<Long> categories) {
        //新增品牌信息表  新增成功后会返回 1 ,否则新增失败
        int count = brandMapper.insertSelective(brand);
        if(count!=1){
            throw  new LyExcception(ExceptionEnum.ADD_BRAND_ERROR);
        }

        // 新增品牌和分类中间表
        for(Long categoriy :categories){
            count = brandMapper.insertCategoryBrand(categoriy, brand.getId());
            if(count!=1){
                throw  new LyExcception(ExceptionEnum.ADD_BRAND_CATEGORY_ERROR);
            }
        }
    }

    /**
     *  根据品牌id删除品牌
     * @param bid
     */
    @Transactional
    public void delBrandById(Long bid) {

        //根据品牌id删除对应的品牌信息
        brandMapper.deleteByPrimaryKey(bid);

        //根据品牌id删除tb_category_brand 中间表中对应的分类id
        brandMapper.delCategoryAndBrandByBid(bid);
    }

    /**
     * 根据品牌id查询品牌信息
     * @param id
     * @return
     */
    public Brand queryBrandById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        //没查到抛出异常
        if(brand ==null){
            throw new LyExcception(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    /**
     * 根据3级分类id查询品牌信息
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCategoryId(Long cid) {
        List<Brand> brands = brandMapper.queryBrandByCategoryId(cid);
        //没查到抛出异常
        if(CollectionUtils.isEmpty(brands)){
            throw new LyExcception(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return  brands;
    }

    /**
     * 根据品牌id集合查询品牌信息
     * @param bids
     * @return
     */
    public List<Brand> queryBrandByIdList(List<Long> bids) {
        List<Brand> brands = brandMapper.selectByIdList(bids);
        //没查到抛出异常
        if(CollectionUtils.isEmpty(brands)){
            throw new LyExcception(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return  brands;
    }
}
