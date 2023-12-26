package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RaceServiceImpl implements RaceService{
    private final RaceRepository raceRepository;

    @Override
    public Race saveRace(Race race) {
        return raceRepository.save(race);
    }

    public List<Race> saveAllRaces(List<Race> races) { return raceRepository.saveAll(races); }

    @Override
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }
}
