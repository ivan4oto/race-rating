package com.ivangochev.raceratingapi.race.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

public record CreateRaceDto(
        String name,
        String description,
        BigDecimal latitude,
        BigDecimal longitude,
        String websiteUrl,
        String logoUrl,
        Set<String> terrainTags,
        BigDecimal distance,
        Integer elevation,
        Date eventDate
) {
}
