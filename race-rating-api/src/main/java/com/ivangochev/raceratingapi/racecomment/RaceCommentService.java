package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.racecomment.RaceCommentResponseDTO;

import java.util.List;

public interface RaceCommentService {
    List<RaceCommentResponseDTO> getRaceCommentsByRaceId(Long raceId);
}
