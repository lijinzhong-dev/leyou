package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/29
 * @Description: com.leyou.upload.service
 * @version: 1.0
 */
@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class) //引入属性配置类
public class UploadService {

    @Autowired //注入配置类
    private UploadProperties uploadProperties;

    //定义允许上传的图片类型(实际开发中要写到配置文件)
    private static  final List<String> ALLOW_FILE_TYPES= Arrays.asList("image/png", "image/jpeg");

    @Autowired
    FastFileStorageClient storageClient;

    /**
     *  上传图片到FastDFS
     * @param file
     * @return
     */
    public String uploadImageToFastDFS(MultipartFile file) {
        String imageUrl="";
        try {

            //校验文件类型（只校验文件类型远远不够,上传时修改文件后缀名即可通过该校验,故需要校验文件内容）
            String fileType = file.getContentType();//获取上传文件的类型
            if(!uploadProperties.getAllowFileTypes().contains(fileType)){
                log.info("上传的文件类型["+fileType+"]不支持!");
                throw new LyExcception(ExceptionEnum.NOT_ALLOW_FILE_TYPE);
            }

            //校验文件内容（如果不是图片返回值是null或者抛出异常）
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                log.info("上传失败，文件内容不符合要求");
                throw new LyExcception(ExceptionEnum.NOT_ALLOW_FILE_CONTENT);
            }

            // 获取文件后缀名
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            // 上传到FastDFS
            StorePath storePath = this.storageClient.uploadFile(
                    file.getInputStream(), file.getSize(), extension, null);

            //返回图片的完整路径url
            return uploadProperties.getBaseUrl() + storePath.getFullPath();

        } catch (IOException e) {
            log.info("上传图片失败!!!");
            throw new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
    }

    /**
     *  上传图片到本地
     * @param file
     * @return
     */
    public String uploadImageToLocal(MultipartFile file) {

        try {

            //校验文件类型（只校验文件类型远远不够,上传时修改文件后缀名即可通过该校验,故需要校验文件内容）
            String fileType = file.getContentType();//获取上传文件的类型
            if(!ALLOW_FILE_TYPES.contains(fileType)){
                log.info("上传的文件类型["+fileType+"]不支持!");
                throw new LyExcception(ExceptionEnum.NOT_ALLOW_FILE_TYPE);
            }

            //校验文件内容（如果不是图片返回值是null或者抛出异常）
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                log.info("上传失败，文件内容不符合要求");
                throw new LyExcception(ExceptionEnum.NOT_ALLOW_FILE_CONTENT);
            }


            //生成本地保存图片的目录
            File dir=new File("E:\\leyouImage");
            if(!dir.exists()){//如果目录不存在自行创建
                dir.mkdirs();
            }
            //保存图片
            file.transferTo(new File(dir,file.getOriginalFilename()));

        } catch (IOException e) {
            log.info("上传图片失败!!!");
            throw new LyExcception(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        //返回图片的url(假的)
        String imageUrl = "http://image.leyou.com/upload/" + file.getOriginalFilename();

        return imageUrl;
    }
}
