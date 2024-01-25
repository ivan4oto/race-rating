package com.ivangochev.raceratingapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {
    private final AwsProperties awsProperties;

    public AwsConfig(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    @Bean
    S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(awsProperties.getRegion())
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(awsProperties.getRegion())
                .build();
    }
}
