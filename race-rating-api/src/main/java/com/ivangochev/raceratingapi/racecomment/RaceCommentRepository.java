package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.racecomment.RaceComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceCommentRepository extends JpaRepository<RaceComment, Long> {
    List<RaceComment> findAllByRaceId(Long raceId);
}
