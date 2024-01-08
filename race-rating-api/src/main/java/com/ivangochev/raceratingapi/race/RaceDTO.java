package com.ivangochev.raceratingapi.race;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RaceDTO {
    private Long id;
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
    private String terrain;
    private BigDecimal distance;
    private Integer elevation;
    private Date eventDate;
    private Long createdById;

}
