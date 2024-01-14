package com.ivangochev.raceratingapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

@Component
public class AwsProperties {

    private final String bucketName;
    private final Region region;

    public AwsProperties(@Value("${aws.s3.bucket}") String bucketName,
                         @Value("${aws.region}") String regionString) {
        this.bucketName = bucketName;
        this.region = Region.of(regionString);
    }

    public String getBucketName() {
        return bucketName;
    }

    public Region getRegion() {
        return region;
    }
}
