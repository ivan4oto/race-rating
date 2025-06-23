package com.ivangochev.raceratingapi.racecomment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RaceCommentRepository extends JpaRepository<RaceComment, Long> {
    boolean existsByAuthorIdAndRaceId(Long authorId, Long raceId);
    List<RaceComment> findAllByRaceId(Long raceId);
    @Query("""
    SELECT new com.ivangochev.raceratingapi.racecomment.RaceCommentWithVotesDto(
        c.id,
        c.commentText,
        c.author.name,
        c.author.imageUrl,
        c.createdAt,
        c.race.id,
        SUM(CASE WHEN v.upvote = true THEN 1 ELSE 0 END),
        SUM(CASE WHEN v.upvote = false THEN 1 ELSE 0 END)
    )
        FROM RaceComment c
        LEFT JOIN c.votes v
        WHERE c.race.id = :raceId
        GROUP BY c.id, c.commentText, c.author.name, c.author.imageUrl, c.createdAt
    """)
    List<RaceCommentWithVotesDto> findCommentsWithVoteCountsByRaceId(@Param("raceId") Long raceId);

}
