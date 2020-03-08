package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description:  统一异常处理类  其本质是一种通知（advice）
 * @version: 1.0
 */
@ControllerAdvice   //默认情况会拦截controller层所有出现的异常,需要引入spring-webmvc依赖
public class CommonExceptionHandler {

    /**
     * 该方法的返回值就是返回到页面的值
     */
    @ExceptionHandler(LyExcception.class)  //指定拦截LyExcception及其子类异常
    public ResponseEntity<ExceptionResult> myHandlerException(LyExcception ex){
        ExceptionEnum em = ex.getExceptionEnum();
        return  ResponseEntity.status(em.getCode()).body(new ExceptionResult(em));
    }

    //如同以上的异常处理方法,针对不同的异常类型可以书写多个

}
