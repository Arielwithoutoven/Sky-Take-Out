package com.sky.utils;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class MinioUtil {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    /**
     * 文件上传
     * @param file 文件
     * @return Boolean
     */
    public String upload(MultipartFile file, String objectName) throws Exception {
        try (MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build()) {
            try {
                PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
                minioClient.putObject(objectArgs);
            } catch (MinioException e) {
                System.out.println("Error occurred: " + e);
                System.out.println("HTTP trace: " + e.httpTrace());
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        String filePath = endpoint +"/browser/"+ bucketName +"/" + objectName;

        log.info("File Uploaded to: {}", filePath);
        return filePath;
    }

    /**
     * 预览图片
     * @param fileName
     * @return
     */
    public String preview(String fileName){
        // 查看文件地址
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(bucketName).object(fileName).method(Method.GET).build();
        try {
            String url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     * @param fileName 文件名称
     * @param res response
     * @return Boolean
     */
    public void download(String fileName, HttpServletResponse res) {
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)){
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()){
                while ((len=response.read(buf))!=-1){
                    os.write(buf,0,len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                // 设置强制下载不打开
                // res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()){
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看文件对象
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects() {
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items;
    }

    /**
     * 删除
     * @param fileName
     * @return
     * @throws Exception
     */
    public boolean remove(String fileName){
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        try {
            minioClient.removeObject( RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        }catch (Exception e){
            return false;
        }
        return true;
    }

}


