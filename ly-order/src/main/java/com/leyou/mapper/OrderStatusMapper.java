package com.leyou.mapper;

import com.leyou.pojo.OrderDetail;
import com.leyou.pojo.OrderStatus;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.mapper
 * @version: 1.0
 */
public interface OrderStatusMapper extends Mapper<OrderStatus>,IdListMapper<OrderStatus,Long> {
}
