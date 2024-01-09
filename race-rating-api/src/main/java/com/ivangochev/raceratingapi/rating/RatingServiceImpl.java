package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.exception.RaceAlreadyExistsException;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceNotFoundException;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final RatingMapper ratingMapper;

    @Override
    public List<Rating> findByRace(Race race) {
       return ratingRepository.findAllByRace(race);
    }

    @Override
    public Rating saveRating(RatingDto ratingDto, User user) {
        Race race = getRaceIfUserNotVoted(user, ratingDto.getRaceId());
        user.getVotedForRaces().add(race);
        Rating ratingToSave = ratingMapper.ratingDtoToRating(ratingDto, user, race);
        userRepository.save(user);

        return ratingRepository.save(ratingToSave);
    }

    @Override
    public List<Rating> saveAllRatings(List<Rating> ratings) {
        return ratingRepository.saveAll(ratings);
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

    private boolean userHasVotedForRace(User user, Race race) {
        return user.getVotedForRaces().contains(race);
    }
}
