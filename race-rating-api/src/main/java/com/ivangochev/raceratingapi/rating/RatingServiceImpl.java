package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceNotFoundException;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final RatingMapper ratingMapper;

    @Override
    public List<RatingDto> findByRace(Race race) {
        return ratingRepository.findAllByRace(race).stream()
                .map(ratingMapper::ratingToRatingDto).collect(Collectors.toList());
    }

    @Override
    public RatingDto saveRating(RatingDto ratingDto, User user) {
        Race race = getRaceIfUserNotVoted(user, ratingDto.getRaceId());
        user.getVotedForRaces().add(race);
        Rating ratingToSave = ratingMapper.ratingDtoToRating(ratingDto, user, race);
        userRepository.save(user);
        Rating savedRating = ratingRepository.save(ratingToSave);
        updateRaceAverageRatings(race, savedRating); // Update the average ratings of the race
        raceRepository.save(race);
        return ratingMapper.ratingToRatingDto(savedRating);
    }

    @Override
    public List<RatingDto> saveAllRatings(List<Rating> ratings) {
        return ratingRepository.saveAll(ratings).stream().map(ratingMapper::ratingToRatingDto).collect(Collectors.toList());
    }

    @Override
    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }

    private Race getRaceIfUserNotVoted(User user, Long raceId) {
        return raceRepository.findById(raceId).map(race -> {
            if (userHasVotedForRace(user, race)) {
                throw new UserAlreadyVotedException("User has already voted for this race!");
            }
            return race;
        }).orElseThrow(() -> new RaceNotFoundException(
                String.format("Race with id %s not found", raceId))
        );
    }

    private void updateRaceAverageRatings(Race race, Rating newRating) {
        int newCount = race.getRatingsCount() + 1;
        race.setRatingsCount(newCount);
        race.setAverageLocationScore(updateAverage(race.getAverageLocationScore(), newRating.getLocationScore(), newCount));
        race.setAverageValueScore(updateAverage(race.getAverageValueScore(), newRating.getValueScore(), newCount));
        race.setAverageTraceScore(updateAverage(race.getAverageTraceScore(), newRating.getTraceScore(), newCount));
        race.setAverageVibeScore(updateAverage(race.getAverageVibeScore(), newRating.getVibeScore(), newCount));
        race.setAverageOrganizationScore(updateAverage(race.getAverageOrganizationScore(), newRating.getOrganizationScore(), newCount));

        race.setAverageRating(calculateOverallAverage(race));
    }

    private BigDecimal updateAverage(BigDecimal currentAverage, int newScore, int totalCount) {
        BigDecimal total = currentAverage.multiply(BigDecimal.valueOf(totalCount - 1)).add(BigDecimal.valueOf(newScore));
        return total.divide(BigDecimal.valueOf(totalCount), MathContext.DECIMAL128);
    }

    private BigDecimal calculateOverallAverage(Race race) {
        BigDecimal total = race.getAverageLocationScore()
                .add(race.getAverageValueScore())
                .add(race.getAverageTraceScore())
                .add(race.getAverageVibeScore())
                .add(race.getAverageOrganizationScore());
        return total.divide(BigDecimal.valueOf(5), MathContext.DECIMAL64);  // Adjust MathContext as needed for precision
    }

    private boolean userHasVotedForRace(User user, Race race) {
        return user.getVotedForRaces().contains(race);
    }
}
