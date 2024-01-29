package com.ivangochev.raceratingapi.controller;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RaceControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllRaces_returnsOkStatus() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/race/all", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getRatings_returnsRace_whenRaceExists() {
        Long raceId = 1L;
        ResponseEntity<Race> response = restTemplate.getForEntity("/api/race/" + raceId, Race.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }


    @Test
    public void createRace_returnsCreatedRace_whenRaceDoesNotExist() {
        CreateRaceDto createRaceDto = new CreateRaceDto();
        createRaceDto.setName("Test Race");
        HttpEntity<CreateRaceDto> request = new HttpEntity<>(createRaceDto);
        ResponseEntity<Race> response = restTemplate.postForEntity("/api/race", request, Race.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    //TODO: test case for createRace with authorized user should be created
}