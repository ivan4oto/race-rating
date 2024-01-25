package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.dto.S3ObjectDto;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Date;

@Service
public class S3ObjectMapper {
    S3ObjectDto map(S3Object s3Object) {
        S3ObjectDto s3ObjectDto = new S3ObjectDto();
        s3ObjectDto.setKey(s3Object.key());
        s3ObjectDto.setLastModified(Date.from(s3Object.lastModified()));
        s3ObjectDto.setSize(s3Object.size());
        return s3ObjectDto;
    }
}
