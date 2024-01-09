package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;

import java.util.List;

public interface RatingService {
    List<Rating> findByRace(Race race);

    Rating saveRating(RatingDto rating, User user);

    List<Rating> saveAllRatings(List<Rating> ratings);

    void deleteRating(Long id);
}
