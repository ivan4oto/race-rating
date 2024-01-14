package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.rating.*;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RatingServiceImplTest {
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RaceRepository raceRepository;
    @Mock
    private RatingMapper ratingMapper;
    @Mock
    private User user;
    @InjectMocks
    private RatingServiceImpl ratingService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testSaveRating() {
        Long raceId = 1L;
        Race race = new Race();
        race.setId(raceId);
        RatingDto ratingDto = new RatingDto();
        ratingDto.setRaceId(raceId);
        Rating rating = new Rating();

        when(raceRepository.findById(anyLong())).thenReturn(Optional.of(race));
        when(user.getVotedForRaces()).thenReturn(new ArrayList<>());
        when(ratingMapper.ratingDtoToRating(any(RatingDto.class), any(User.class), any(Race.class))).thenReturn(rating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(userRepository.save(any(User.class))).thenReturn(user);

        Rating savedRating = ratingService.saveRating(ratingDto, user);

        assertNotNull(savedRating);
        verify(ratingRepository).save(any(Rating.class));
        verify(userRepository).save(user);
    }
    @Test
    public void testSaveAllRatings() {
        List<Rating> ratingsToSave = Arrays.asList(new Rating(), new Rating());

        when(ratingRepository.saveAll(anyIterable())).thenReturn(ratingsToSave);

        List<Rating> savedRatings = ratingService.saveAllRatings(ratingsToSave);

        assertEquals(2, savedRatings.size());
        verify(ratingRepository).saveAll(anyIterable());
    }
    @Test
    public void testSaveRatingThrowsExceptionForAlreadyVoted() {
        Long raceId = 1L;
        Race race = new Race();
        race.setId(raceId);
        RatingDto ratingDto = new RatingDto();
        ratingDto.setRaceId(raceId);
        List<Race> userVotedForRaces = new ArrayList<>();
        userVotedForRaces.add(race);

        when(raceRepository.findById(anyLong())).thenReturn(Optional.of(race));
        when(user.getVotedForRaces()).thenReturn(userVotedForRaces);

        Exception exception = assertThrows(UserAlreadyVotedException.class, () -> {
            ratingService.saveRating(ratingDto, user);
        });

        assertEquals("User has already voted for this race!", exception.getMessage());
    }
    @Test
    public void testFindByRace() {
        Race race = new Race();
        race.setId(1L);
        List<Rating> ratings = Collections.singletonList(new Rating());

        when(ratingRepository.findAllByRace(any(Race.class))).thenReturn(ratings);

        List<Rating> foundRatings = ratingService.findByRace(race);

        assertFalse(foundRatings.isEmpty());
        assertEquals(1, foundRatings.size());
        verify(ratingRepository).findAllByRace(any(Race.class));
    }
}
