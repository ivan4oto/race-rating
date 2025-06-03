package com.ivangochev.raceratingapi.racecomment.vote;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVoteResponseDTO {
    private boolean voteRegistered;
    private Boolean currentVote; // true = upvote, false = downvote, null = no vote
}
