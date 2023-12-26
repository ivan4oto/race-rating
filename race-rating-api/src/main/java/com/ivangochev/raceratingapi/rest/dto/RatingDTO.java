package com.ivangochev.raceratingapi.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class RatingDTO {

    private Long id;
    private Long raceId; // ID of the associated Race
    private Long authorId; // ID of the User who authored the rating

    private int traceScore; // 1 to 5
    private int vibeScore; // 1 to 5
    private int organizationScore; // 1 to 5
    private int locationScore; // 1 to 5
    private int valueScore; // 1 to 5

    private boolean isComingBack;

    private String positiveFeedback;
    private String negativeFeedback;

    private Date createdAt;
}