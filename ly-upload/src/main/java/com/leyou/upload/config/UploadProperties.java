package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/29
 * @Description: 上传文件的一些属性（为了不写死在代码里）
 * @version: 1.0
 */
@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {
    private String baseUrl;
    private List<String> allowFileTypes;
}
