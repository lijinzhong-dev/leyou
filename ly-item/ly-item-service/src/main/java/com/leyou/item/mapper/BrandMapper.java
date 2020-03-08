package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/27
 * @Description: 注意：自定义的通用mapper是接口,要继承Mapper<T>
 * @version: 1.0
 */
public interface BrandMapper extends Mapper<Brand> ,IdListMapper<Brand,Long>{

    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     *  根据品牌id删除分类品牌中间表信息
     * @param bid
     * @return
     */
    @Delete("DELETE FROM tb_category_brand WHERE BRAND_ID = #{bid}")
    int delCategoryAndBrandByBid(@Param("bid") Long bid);

    /**
     * 根据3级分类id查询品牌信息
     * @param cid
     * @return
     */
    @Select("SELECT ID,NAME,IMAGE,LETTER FROM tb_brand,tb_category_brand WHERE ID=BRAND_ID AND CATEGORY_ID = #{cid}")
    List<Brand> queryBrandByCategoryId(@Param("cid") Long cid);
}
