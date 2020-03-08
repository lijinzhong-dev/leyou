package com.leyou.item.mapper;

import com.leyou.item.pojo.Sku;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/6
 * @Description: com.leyou.item.mapper
 * @version: 1.0
 */
public interface SkuMapper extends Mapper<Sku>,IdListMapper<Sku,Long> {
}
