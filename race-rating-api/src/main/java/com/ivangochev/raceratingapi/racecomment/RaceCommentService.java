package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.racecomment.vote.CommentVoteResponseDTO;
import com.ivangochev.raceratingapi.user.User;

import java.util.List;

public interface RaceCommentService {
    List<RaceCommentWithVotesDto> getRaceCommentsByRaceId(Long raceId);
    RaceCommentResponseDTO createRaceComment(RaceCommentRequestDTO comment, User user, Long raceId);
    CommentVoteResponseDTO voteComment(Long commentId, boolean isUpVote, User user);

}
