package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/30
 * @Description:
 * @version: 1.0
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    public List<Category> queryCategoryByPid(Long pid) {

        //查询条件
        Category category = new Category();
        category.setParentId(pid);
        /**
         * categoryMapper.select(Category category)
         * 会根据实体类Category中非空属性作为查询条件
         */
        List<Category> list = categoryMapper.select(category);

        //判断查询结果（当没查询到结果时,根据rest风格需要返回状态码404）
        if(CollectionUtils.isEmpty(list)){
            throw  new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }
    /**
     * 新增商品分类节点
     */
    @Transactional
    public void addCategoryId(Category category) {

        //新增分类节点
        categoryMapper.insert(category);

        //修改父节点字段 parent_id
        modifyParentId(category,true);


    }

    private void modifyParentId(Category category,boolean flag) {
        //修改新增节点的父节点IsParent字段为true
        Category parent=new Category();
        parent.setId(category.getParentId());
        parent.setIsParent(flag);

        //更新父节点IsParent字段为true
        categoryMapper.updateByPrimaryKeySelective(parent);
    }

    public void updateCategoryId(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Transactional
    public void delCategoryId(Long id) {

        Category category = categoryMapper.selectByPrimaryKey(id);

        if(category.getIsParent()){
           throw new LyExcception(ExceptionEnum.HAS_SUB_TREE);
        }

        List<Category> categories = this.queryCategoryByPid(category.getParentId());

        //如果没有同级分类，则说明非父级节点 即 parent_id=fasle
        if(!CollectionUtils.isEmpty(categories)){
            if(categories.size() == 1){
                //修改父节点字段 parent_id
                modifyParentId(category,false);
            }
        }

        //根据id删除商品分类
        categoryMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据品牌id查询分类
     * @param bid
     * @return
     */
    public List<Category> queryCatogeriesByBid(Long bid) {
        List<Category> categories = categoryMapper.queryCatogeriesByBid(bid);

        if(CollectionUtils.isEmpty(categories)){
            throw  new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    /**
     * 通过多个id进行查询的前提是：
     * CategoryMapper类必须继承IdListMapper<Category,Long>
     * 其中Category是实体类，Long是主键id的类型
     */
    public List<Category> queryByCids(List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);

        if(CollectionUtils.isEmpty(categories)){
            throw  new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    /**
     * 根据分类级别3，即cid3，查询出1-3级别的分类用list集合包装
     * @param cid3
     * @return
     */
    public List<Category> queryCategoryByCid3(Long cid3) {
        //根据3级分类id查询
        Category c3 = this.categoryMapper.selectByPrimaryKey(cid3);
        if(c3 ==null){
            throw  new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }



        //根据2级分类id查询
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        if(c2 ==null){
            throw  new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        //根据1级分类id查询
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        if(c1==null){
            throw  new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<Category> categories = Arrays.asList(c1, c2, c3);
        return categories;
    }
}
