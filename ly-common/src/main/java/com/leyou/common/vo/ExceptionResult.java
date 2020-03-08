package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

/**
 * @Auther: lijinzhong
 * @Date: 2019/8/27
 * @Description: 异常结果信息  key-value形式
 *
 */
@Data
public class ExceptionResult {

    private int status;//状态码

    private String msg;//错误信息

    private  Long timestamp;//时间戳

    public ExceptionResult(ExceptionEnum em) {
        this.status=em.getCode();
        this.msg=em.getMsg();
        this.timestamp=System.currentTimeMillis();
    }
}
