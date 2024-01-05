package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.RaceComment;
import com.ivangochev.raceratingapi.rest.dto.RaceCommentResponseDTO;

import java.util.List;

public interface RaceCommentService {
    List<RaceCommentResponseDTO> getRaceCommentsByRaceId(Long raceId);
}
