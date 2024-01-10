package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class RatingMapper {
    public Rating ratingDtoToRating(RatingDto ratingDto, User user, Race race) {
        Rating newRating = new Rating();
        newRating.setRace(race);
        newRating.setAuthor(user);
        newRating.setTraceScore(ratingDto.getTraceScore());
        newRating.setVibeScore(ratingDto.getVibeScore());
        newRating.setOrganizationScore(ratingDto.getOrganizationScore());
        newRating.setLocationScore(ratingDto.getLocationScore());
        newRating.setValueScore(ratingDto.getValueScore());
        newRating.setCreatedAt(new Date());

        return newRating;
    }
}
