package com.leyou.mapper;

import com.leyou.user.pojo.User;
import tk.mybatis.mapper.common.Mapper;

/**
 * 使用通用mapper能够自动生成泛型中实体类对应的表的相关增删改查sql
 */
public interface UserMapper extends Mapper<User> {
}