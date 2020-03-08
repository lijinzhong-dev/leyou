package com.leyou.upload.web;

import com.leyou.upload.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/29
 * @Description: com.leyou.upload.web
 * @version: 1.0
 */
@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传图片
     * @param file 上传的图片会被springmvc封装到对象MultipartFile中
     * @return
     */
    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){

        String imageUrl=uploadService.uploadImageToFastDFS(file);
        if(StringUtils.isBlank(imageUrl)){
            // url为空，证明上传失败
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        // 返回200，并且携带url路径
        return ResponseEntity.ok(imageUrl);
    }
}
