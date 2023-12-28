package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.Race;

import java.util.List;
import java.util.Optional;

public interface RaceService {
    Race saveRace(Race race);
    List<Race> saveAllRaces(List<Race> races);
    List<Race> getAllRaces();
    Optional<Race> getRaceById(Long raceId);

}
