package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.Rating;
import com.ivangochev.raceratingapi.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Override
    public List<Rating> findByRace(Race race) {
       return ratingRepository.findAllByRace(race);
    }

    @Override
    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }
}
