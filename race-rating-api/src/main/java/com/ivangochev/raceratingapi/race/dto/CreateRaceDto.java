package com.ivangochev.raceratingapi.race.dto;

import java.math.BigDecimal;
import java.util.Date;

public record CreateRaceDto(
        String name,
        String description,
        BigDecimal latitude,
        BigDecimal longitude,
        String websiteUrl,
        String logoUrl,
        String terrain,
        BigDecimal distance,
        Integer elevation,
        Date eventDate
) {
}
