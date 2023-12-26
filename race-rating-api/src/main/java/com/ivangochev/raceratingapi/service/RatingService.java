package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.Rating;

import java.util.List;

public interface RatingService {
    List<Rating> findByRace(Race race);

    Rating saveRating(Rating rating);

    List<Rating> saveAllRatings(List<Rating> ratings);

    void deleteRating(Long id);
}
