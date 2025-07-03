package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.Race;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RaceRepository extends JpaRepository<Race, Long> {
    Optional<Race> findRaceByName(String name);
    Optional<Race> deleteRaceById(Long raceId);
}
