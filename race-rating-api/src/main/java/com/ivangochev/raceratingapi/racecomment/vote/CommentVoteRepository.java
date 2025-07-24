package com.ivangochev.raceratingapi.racecomment.vote;

import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    Optional<CommentVote> findByCommentAndVoter(RaceComment comment, User user);
    @Query("SELECT cv.comment.id AS commentId, cv.upvote AS isUpvote " +
            "FROM CommentVote cv " +
            "WHERE cv.voter.id = :voterId AND cv.comment.id IN :commentIds")
    List<UserCommentVoteStatus> findByVoterIdAndCommentIdIn(
            @Param("voterId") Long voterId,
            @Param("commentIds") List<Long> commentIds
    );
}
