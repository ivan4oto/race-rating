package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.RaceComment;

import java.util.List;

public interface RaceCommentService {
    List<RaceComment> getRaceCommentsByRaceId(Long raceId);
}
