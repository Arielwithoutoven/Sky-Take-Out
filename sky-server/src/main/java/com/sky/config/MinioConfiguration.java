package com.sky.config;


import com.sky.properties.MinioProperties;
import com.sky.utils.MinioUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建 MinioUtil 对象
 */
@Configuration
@AllArgsConstructor
@Slf4j
public class MinioConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MinioUtil minioUtil(MinioProperties minioProperties) {
        log.info("Starting Creating MinioUtil Object: {}", minioProperties);
        return new MinioUtil(minioProperties.getEndpoint(), minioProperties.getAccessKey(), minioProperties.getSecretKey(), minioProperties.getBucketName());
    }
}
