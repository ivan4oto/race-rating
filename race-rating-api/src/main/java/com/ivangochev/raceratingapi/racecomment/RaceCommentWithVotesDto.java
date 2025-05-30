package com.ivangochev.raceratingapi.racecomment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceCommentWithVotesDto {
    private Long id;
    private String commentText;
    private String authorName;
    private String authorImageUrl;
    private Date createdAt;
    private Long upvoteCount;
    private Long downvoteCount;
}
