package com.ivangochev.raceratingapi.race.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@Getter
public class RaceSummaryDto {
    private final Long id;
    private final String name;
    private final String logoUrl;
    private final BigDecimal averageRating;
    private final int ratingsCount;
    private final long totalComments;
    private final long totalVotes;
    private final Date eventDate;
}