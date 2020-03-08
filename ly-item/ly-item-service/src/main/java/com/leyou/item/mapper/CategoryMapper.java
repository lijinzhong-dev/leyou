package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/30
 * @Description: 该类继承Mapper<Category>后,会自动生成
 *               关于该类对应表tb_category的增删改查方法
 *               继承IdListMapper<Category,Long>的主要是
 *               为了可以通过多个id进行查询其中Category是实体类，Long是主键id的类型
 * @version: 1.0
 */
public interface CategoryMapper extends Mapper<Category> ,IdListMapper<Category,Long> {

    /**
     * 新增商品分类和品牌中间表数据
     * @param bid 品牌id
     * @return
     */
    @Select("SELECT ID, NAME,PARENT_ID,IS_PARENT,SORT FROM tb_category_brand, tb_category WHERE  CATEGORY_ID = ID AND BRAND_ID=#{bid} ")
    List<Category> queryCatogeriesByBid(@Param("bid") Long bid);
}
