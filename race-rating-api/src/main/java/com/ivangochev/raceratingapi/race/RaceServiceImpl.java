package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.exception.RaceAlreadyExistsException;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RaceServiceImpl implements RaceService{
    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;

    @Override
    public Race createRace(CreateRaceDto raceDto, User user) {
        return raceMapper.createRaceDtoToRace(raceDto, user);
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

    @Override
    public void validateRaceDoesNotExist(String name) {
        Optional<Race> race = raceRepository.findRaceByName(name);
        if (race.isPresent()) {
            throw new RaceAlreadyExistsException(String.format("Race with name %s already exists!", name));
        }
    }
}
