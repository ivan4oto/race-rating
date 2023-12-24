package com.ivangochev.raceratingapi.repository;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findAllByRace(Race race);

}
