package com.ivangochev.raceratingapi.utils.aws;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;

@Service
public class S3PresignedUrlGenerator {
    private final S3Presigner presigner;

    public S3PresignedUrlGenerator(S3Presigner presigner) {
        this.presigner = presigner;
    }

    public URL generatePresignedUrl(String bucketName, String objectKey, int expirationMinutes) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(p -> p.signatureDuration(Duration.ofMinutes(expirationMinutes))
                .putObjectRequest(objectRequest));

        URL url = presignedRequest.url();
        presigner.close();

        return url;
    }
}
