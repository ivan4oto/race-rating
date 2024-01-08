package com.ivangochev.raceratingapi.race;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Race> getRaceById(Long raceId) {
        return raceRepository.findById(raceId);
    }
}
