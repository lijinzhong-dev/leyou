package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.IdListMapper;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/6
 * @Description: com.leyou.item.mapper
 * @version: 1.0
 */
public interface StockMapper extends BaseMapper<Stock>,IdListMapper<Stock,Long> {

    /**
     * update tb_stock
     *  set stock = stock - #{num}
     * where sku_id = #{skuId} and stock >= #{num}
     * 注意：条件stock >= #{num}主要是为了多线程条件下防止库存一直减少，减少到负值
     * @param skuId
     * @param num
     */
    @Update("update tb_stock set stock = stock - #{num} where sku_id = #{skuId} and stock >= #{num}")
    int decreaseStock(@Param("skuId")Long skuId,@Param("num") Integer num);
}
