package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sky.minio")
@Data
@Component
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
