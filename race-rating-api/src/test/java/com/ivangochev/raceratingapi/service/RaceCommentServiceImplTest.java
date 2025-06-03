package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.racecomment.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RaceCommentServiceImplTest {

    @InjectMocks
    private RaceCommentServiceImpl raceCommentService;

    @Mock
    private RaceCommentRepository commentRepository;

    @Test
    public void testGetRaceCommentsByRaceId() {
        Long raceId = 1L;
        Date dummyDate = new Date(0);
        RaceComment raceComment = new RaceComment();
        RaceCommentWithVotesDto raceCommentResponseDTO = new RaceCommentWithVotesDto(
                1L,
                "comment",
                "author",
                "http://image.com/image.jpeg",
                dummyDate,
                1L,
                0L

        );
        List<RaceComment> comments = Arrays.asList(raceComment);
        List<RaceCommentWithVotesDto> expectedResponse = Arrays.asList(raceCommentResponseDTO);

        when(commentRepository.findCommentsWithVoteCountsByRaceId(raceId)).thenReturn(List.of(raceCommentResponseDTO));

        List<RaceCommentWithVotesDto> actualResponse = raceCommentService.getRaceCommentsByRaceId(raceId);

        verify(commentRepository, times(1)).findCommentsWithVoteCountsByRaceId(raceId);
        assertEquals(expectedResponse, actualResponse);
    }
}