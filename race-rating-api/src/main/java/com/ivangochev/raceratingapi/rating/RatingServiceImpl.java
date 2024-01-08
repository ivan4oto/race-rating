package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RaceRepository raceRepository;

    @Override
    public List<Rating> findByRace(Race race) {
       return ratingRepository.findAllByRace(race);
    }

    @Override
    public Rating saveRating(Rating rating) {
        Race race = rating.getRace();
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> saveAllRatings(List<Rating> ratings) {
        return ratingRepository.saveAll(ratings);
    }

    @Override
    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }
}
