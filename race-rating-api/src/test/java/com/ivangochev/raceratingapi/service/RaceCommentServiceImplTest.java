package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.racecomment.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RaceCommentServiceImplTest {

    @InjectMocks
    private RaceCommentServiceImpl raceCommentService;

    @Mock
    private RaceCommentRepository commentRepository;

    @Mock
    private RaceCommentMapper commentMapper;

    @Test
    public void testGetRaceCommentsByRaceId() {
        Long raceId = 1L;
        RaceComment raceComment = new RaceComment();
        RaceCommentResponseDTO raceCommentResponseDTO = new RaceCommentResponseDTO();
        List<RaceComment> comments = Arrays.asList(raceComment);
        List<RaceCommentResponseDTO> expectedResponse = Arrays.asList(raceCommentResponseDTO);

        when(commentRepository.findAllByRaceId(raceId)).thenReturn(comments);
        when(commentMapper.toRaceCommentResponseDTO(raceComment)).thenReturn(raceCommentResponseDTO);

        List<RaceCommentResponseDTO> actualResponse = raceCommentService.getRaceCommentsByRaceId(raceId);

        verify(commentRepository, times(1)).findAllByRaceId(raceId);
        verify(commentMapper, times(1)).toRaceCommentResponseDTO(raceComment);
        assertEquals(expectedResponse, actualResponse);
    }
}