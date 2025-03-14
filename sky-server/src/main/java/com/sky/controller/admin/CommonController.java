package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.MinioUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags= "通用接口")
public class CommonController {
    @Autowired
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("File Uploading: {}", file);
        try {
            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;

            String filePath = minioUtil.upload(file, objectName);
            return Result.success(filePath);
        } catch (Exception e) {
            log.error("File Upload Failed: ", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);        }
    }
}
