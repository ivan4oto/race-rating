package com.ivangochev.raceratingapi.rating;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivangochev.raceratingapi.config.CustomTestConfig;
import com.ivangochev.raceratingapi.factory.MockDataFactory;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.security.TokenAuthenticationFilter;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = RatingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomTestConfig.class)
class RatingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RatingService ratingService;
    @MockBean
    private UserService userService;
    @MockBean
    private RaceRepository raceRepository;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testGetRatings_raceExists() throws Exception {
        Long raceId = 1L;
        Race race = MockDataFactory.createTestRace();
        race.setId(raceId);
        Rating rating1 = new Rating();
        rating1.setId(1L);
        rating1.setRace(race);
        rating1.setValueScore(3);
        Rating rating2 = new Rating();
        rating2.setId(2L);
        rating2.setValueScore(5);

        when(raceRepository.findById(raceId)).thenReturn(Optional.of(race));
        when(ratingService.findByRace(race)).thenReturn(Arrays.asList(rating1, rating2));

        mockMvc.perform(get("/api/ratings/race/{raceId}", raceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].valueScore").value(3))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].valueScore").value(5));
        verify(raceRepository, times(1)).findById(raceId);
        verify(ratingService, times(1)).findByRace(race);
    }
    @Test
    public void testGetRatings_raceNotFound() throws Exception {
        Long raceId = 1L;

        when(raceRepository.findById(raceId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ratings/race/{raceId}", raceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(raceRepository, times(1)).findById(raceId);
        verify(ratingService, never()).findByRace(any());
    }
    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "ivan", userDetailsServiceBeanName = "userDetailsServiceMock")
    public void testCreateRating_Success() throws Exception {
        User user = MockDataFactory.createTestUser();

        RatingDto ratingDto = new RatingDto();
        ratingDto.setId(1L);
        ratingDto.setValueScore(3);
        ratingDto.setLocationScore(5);
        ratingDto.setAuthorId(1L);

        Rating savedRating = new Rating();
        savedRating.setId(1L);
        savedRating.setValueScore(3);
        savedRating.setLocationScore(5);
        savedRating.setOrganizationScore(5);
        savedRating.setTraceScore(5);
        savedRating.setVibeScore(5);
        savedRating.setAuthor(user);
        savedRating.setCreatedAt(new Date());
        savedRating.setAuthor(user);

        when(userService.validateAndGetUserByUsername("ivan")).thenReturn(user);
        when(ratingService.saveRating(any(RatingDto.class), eq(user))).thenReturn(savedRating);

        // When & Then
        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.valueScore").value(ratingDto.getValueScore()))
                .andExpect(jsonPath("$.locationScore").value(ratingDto.getLocationScore()))
                .andExpect(jsonPath("$.author.id").value(ratingDto.getAuthorId()));

        verify(userService, times(1)).validateAndGetUserByUsername("ivan");
        verify(ratingService, times(1)).saveRating(any(RatingDto.class), eq(user));
    }
}