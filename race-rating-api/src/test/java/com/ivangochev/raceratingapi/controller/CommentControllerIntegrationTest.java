package com.ivangochev.raceratingapi.controller;

import com.ivangochev.raceratingapi.racecomment.RaceCommentResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllCommentsForRace_returnsOkStatus() {
        Long raceId = 1L;
        ResponseEntity<List> response = restTemplate.getForEntity("/public/comments/race/" + raceId, List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getAllCommentsForRace_returnsListOfComments() {
        Long raceId = 1L;
        ResponseEntity<RaceCommentResponseDTO[]> response = restTemplate.getForEntity("/public/comments/race/" + raceId, RaceCommentResponseDTO[].class);
        assertThat(response.getBody()).isNotNull();
    }
}