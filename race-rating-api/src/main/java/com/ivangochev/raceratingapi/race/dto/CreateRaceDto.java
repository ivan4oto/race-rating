package com.ivangochev.raceratingapi.race.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public final class CreateRaceDto {
    private String name;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String websiteUrl;
    private String logoUrl;
    private String terrain;
    private BigDecimal distance;
    private Integer elevation;
    private Date eventDate;
}
