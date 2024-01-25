package com.ivangochev.raceratingapi.race.dto;

import lombok.Data;
import java.util.Date;

@Data
public class S3ObjectDto {
    private String key;
    private Long size;
    private Date lastModified;
    private String fullUrl;
    private String name;
}
