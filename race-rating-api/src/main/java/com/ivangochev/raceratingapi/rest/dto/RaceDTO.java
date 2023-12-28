package com.ivangochev.raceratingapi.rest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RaceDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal averageRating;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String websiteUrl;
    private String logoUrl;
    private String terrain;
    private BigDecimal distance;
    private Integer elevation;
    private Date eventDate;
    private Long createdById;

}
