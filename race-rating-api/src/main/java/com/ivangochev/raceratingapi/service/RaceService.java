package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.Race;

import java.util.List;

public interface RaceService {
    Race saveRace(Race race);
    List<Race> saveAllRaces(List<Race> races);
}
