package com.ivangochev.raceratingapi.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.Map;

@Service
@Slf4j
public class S3UploadFileService {
    private final S3Client s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3UploadFileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(String key, byte[] content) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("image/png")
                    .metadata(Map.of("uploaded-by", "RaceService"))
                    .build();

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(content));

            if (response.sdkHttpResponse() == null || !response.sdkHttpResponse().isSuccessful()) {
                throw new RuntimeException("Failed to upload file to S3: " + response);
            }

        } catch (Exception e) {
            // Log or rethrow depending on your error strategy
            log.error("Error uploading file to S3", e);
        }
    }
}
