package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.user.User;

import java.util.List;
import java.util.Optional;

public interface RaceService {
    Race createRace(CreateRaceDto race, User user);

    void saveAllRaces(List<Race> races);
    List<RaceDto> getAllRaces();
    RaceDto getRaceById(Long raceId);
    void validateRaceDoesNotExist(String name);

}
