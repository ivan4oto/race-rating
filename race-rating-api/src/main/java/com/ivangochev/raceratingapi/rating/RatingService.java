package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.race.Race;

import java.util.List;

public interface RatingService {
    List<Rating> findByRace(Race race);

    Rating saveRating(Rating rating);

    List<Rating> saveAllRatings(List<Rating> ratings);

    void deleteRating(Long id);
}
