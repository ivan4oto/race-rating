package com.ivangochev.raceratingapi.repository;

import com.ivangochev.raceratingapi.model.RaceComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceCommentRepository extends JpaRepository<RaceComment, Long> {
    List<RaceComment> findAllByRaceId(Long raceId);
}
