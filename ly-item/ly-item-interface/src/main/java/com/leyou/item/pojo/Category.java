package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 该实体类使用了JPA完成与对应表及字段的映射关系,故需要在该项目的pom中引入依赖persistence-api
 */
@Table(name="tb_category") //使用了JPA
@Data              //使用了lombok
public class Category {
	@Id		//使用了JPA
	@KeySql(useGeneratedKeys=true)//使用了通用mapper
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent;
	private Integer sort;
}