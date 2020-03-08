package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description: 自定义异常
 * @version: 1.0
 */
@NoArgsConstructor  //无参构造器
@AllArgsConstructor //全参构造器
@Getter             //为该类中的属性生成get方法
public class LyExcception extends  RuntimeException{
    private ExceptionEnum exceptionEnum;
}
