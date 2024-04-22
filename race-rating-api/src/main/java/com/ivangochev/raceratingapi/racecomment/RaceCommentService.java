package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.racecomment.RaceCommentResponseDTO;
import com.ivangochev.raceratingapi.user.User;

import java.util.List;

public interface RaceCommentService {
    List<RaceCommentResponseDTO> getRaceCommentsByRaceId(Long raceId);
    RaceCommentResponseDTO createRaceComment(RaceCommentRequestDTO comment, User user, Long raceId);
}
