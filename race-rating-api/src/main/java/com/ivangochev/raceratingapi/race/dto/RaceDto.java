package com.ivangochev.raceratingapi.race.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RaceDto {
    private Long id;
    private int ratingsCount;
    private String name;
    private String description;
    private BigDecimal averageRating;
    private BigDecimal averageTraceScore;
    private BigDecimal averageVibeScore;
    private BigDecimal averageOrganizationScore;
    private BigDecimal averageLocationScore;
    private BigDecimal averageValueScore;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String websiteUrl;
    private String logoUrl;
    private Date eventDate;
    private Long authorId;

}
