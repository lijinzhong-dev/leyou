package com.leyou.mapper;

import com.leyou.pojo.Order;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.mapper
 * @version: 1.0
 */
public interface OrderMapper extends Mapper<Order>,IdListMapper<Order,Long> {
}
