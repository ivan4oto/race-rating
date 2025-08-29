package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceSummaryDto;
import com.ivangochev.raceratingapi.user.User;

import java.util.List;

public interface RaceService {
    RaceDto createRace(CreateRaceDto race, User user);
    RaceDto editRace(Long raceId, CreateRaceDto raceDto);

    void saveAllRaces(List<Race> races);
    List<RaceSummaryDto> getAllRaces();
    RaceDto getRaceById(Long raceId);
    void validateRaceDoesNotExist(String name);
    boolean isRaceOwner(Long raceId, User user);
    void deleteRace(Long raceId);
}
