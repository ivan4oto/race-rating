package com.ivangochev.raceratingapi.repository;

import com.ivangochev.raceratingapi.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceRepository extends JpaRepository<Race, Long> {

}
