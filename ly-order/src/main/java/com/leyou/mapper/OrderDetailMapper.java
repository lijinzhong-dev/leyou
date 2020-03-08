package com.leyou.mapper;

import com.leyou.pojo.Order;
import com.leyou.pojo.OrderDetail;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.mapper
 * @version: 1.0
 */
public interface OrderDetailMapper extends Mapper<OrderDetail>,IdListMapper<OrderDetail,Long>,IdsMapper<OrderDetail>,InsertListMapper<OrderDetail> {
}
