package com.ivangochev.raceratingapi.config.e2e;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * AWS S3 configuration used exclusively during end-to-end (E2E) testing.
 *
 * <p>This configuration is activated only when the {@code e2e} Spring profile is active.
 * It provides mock AWS beans configured to use a custom endpoint and static test credentials.
 *
 *
 * <p>Note: The credentials used here are static and not meant for production use.
 */
@Configuration
@Profile("e2e")
public class AwsE2EConfig {

    @Bean
    public S3Client s3Client(@Value("${aws.s3.endpoint}") String endpoint,
                             @Value("${aws.region}") String region) {
        System.out.println("Using endpoint: " + endpoint);
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .forcePathStyle(true) // otherwise it throws an unknown host exception...
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")))
                .build();
    }

    @Bean
    S3Presigner s3Presigner(@Value("${aws.s3.endpoint}") String endpoint,
                            @Value("${aws.region}") String region) {
        System.out.println("Using endpoint: " + endpoint);
        return S3Presigner.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")))
                .build();
    }
}
