package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;

import java.util.List;

public interface RatingService {
    List<RatingDto> findByRace(Race race);

    RatingDto saveRating(RatingDto rating, User user);

    List<RatingDto> saveAllRatings(List<Rating> ratings);

    void deleteRating(Long id);
}
