package com.ivangochev.raceratingapi.racecomment;

public record VoteRequest(Long commentId, boolean isUpVote) {
}
