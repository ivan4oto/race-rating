package com.ivangochev.raceratingapi.racecomment.vote;

import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    Optional<CommentVote> findByCommentAndVoter(RaceComment comment, User user);
}
