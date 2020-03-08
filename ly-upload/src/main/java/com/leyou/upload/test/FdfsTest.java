package com.leyou.upload.test;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.LyUploadApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

/**
 * fastdfd客户端使用测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyUploadApplication.class)//classes指定本项目的启动类
public class FdfsTest {

    @Autowired
    private FastFileStorageClient storageClient;


    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * 传统上传文件到fastdfs 使用storageClient.uploadFile(...)
     */
    @Test
    public void testUpload() throws FileNotFoundException {

        File file = new File("E:\\a.jpg");

        /**
         * 上传并且生成缩略图,需要三个参数分别是：
         *  1.流
         *  2.文件的大小
         *  3.文件后缀名成
         *  4.文件的元数据 MateData
         */

        StorePath storePath = this.storageClient.uploadFile(
                new FileInputStream(file), file.length(), "jpg", null);

        // 带分组的路径 如：group1/M00/00/00/wKhWil2REvKAAPdTAAXQ4RCVPws703.png
        System.out.println("带分组的路径: "+storePath.getFullPath());

        // 不带分组的路径 如：M00/00/00/wKhWil2REvKAAPdTAAXQ4RCVPws703.png
        System.out.println("不带分组的路径: "+storePath.getPath());
    }

    /**
     * 上传文件到fastdfs并生成缩略图，使用storageClient.uploadImageAndCrtThumbImage(...)
     */
    @Test
    public void testUploadAndCreateThumb() throws FileNotFoundException {

        File file = new File("C:\\Users\\user\\Desktop\\20190929202124.png");

        // 上传并且生成缩略图
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(
                new FileInputStream(file), file.length(), "png", null);

        // 带分组的路径
        System.out.println("带分组的路径: "+storePath.getFullPath());

        // 不带分组的路径
        System.out.println("不带分组的路径: "+storePath.getPath());

        // 获取缩略图路径
        String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
        System.out.println("缩略图路径: "+path);
    }

    /**
     * 测试文件下载
     */
    @Test
    public void download() {
        try {
            //指定要下的文件id
            byte[] bytes = storageClient.downloadFile("group1",
                                                      "M00/00/00/wKhWil2RIHmARl5qABkG_ZTGqg8914.png",
                                                      new DownloadByteArray());
            //默认下载到项目的根目录下，如：/ly-upload
            FileOutputStream stream = new FileOutputStream("a.jpg");

            //下载到指定的路径下
            //FileOutputStream stream = new FileOutputStream("E:\\a.jpg");

            stream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试文件删除  此删除浏览器会有缓存存在，建议清理一下浏览器缓存
     */
    @Test
    public void deleteFile(){
        //指定要删除的文件id
        storageClient.deleteFile("group1","M00/00/00/wKhWil2RIHmARl5qABkG_ZTGqg8914.png");
    }

}
