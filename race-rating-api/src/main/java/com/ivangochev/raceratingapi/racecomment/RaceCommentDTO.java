package com.ivangochev.raceratingapi.racecomment;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class RaceCommentDTO {

    private Long id;
    private Long raceId;

    private String commentText;
    private Date createdAt;
    private Long authorId;
}
