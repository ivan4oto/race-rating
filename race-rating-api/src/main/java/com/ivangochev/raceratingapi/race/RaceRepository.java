package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.Race;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceRepository extends JpaRepository<Race, Long> {

}
