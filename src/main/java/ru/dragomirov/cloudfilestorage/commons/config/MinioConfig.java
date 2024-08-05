package ru.dragomirov.cloudfilestorage.commons.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.root.user}")
    String accessKey;

    @Value("${minio.root.password}")
    String accessSecret;

    @Value("${minio.url}")
    String minioUrl;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build();
    }
}
