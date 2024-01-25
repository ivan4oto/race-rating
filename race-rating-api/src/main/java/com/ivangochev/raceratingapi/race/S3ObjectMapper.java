package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.dto.S3ObjectDto;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Date;

@Service
public class S3ObjectMapper {
    S3ObjectDto map(S3Object s3Object, String bucketName) {
        S3ObjectDto s3ObjectDto = new S3ObjectDto();
        s3ObjectDto.setKey(s3Object.key());
        s3ObjectDto.setLastModified(Date.from(s3Object.lastModified()));
        s3ObjectDto.setSize(s3Object.size());
        s3ObjectDto.setFullUrl(getFullUrl(bucketName, s3Object.key()));
        s3ObjectDto.setName(getName(s3Object.key()));

        return s3ObjectDto;
    }

    private String getFullUrl(String bucketName, String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }

    private String getName(String key) {
        return key.substring(key.lastIndexOf('/') + 1);
    }
}
