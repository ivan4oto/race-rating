package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.exception.RaceAlreadyExistsException;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
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
        Race raceToCreate = raceMapper.createRaceDtoToRace(raceDto, user);
        return raceRepository.save(raceToCreate);
    }
    public void saveAllRaces(List<Race> races) {
        raceRepository.saveAll(races);
    }
    @Override
    public List<RaceDto> getAllRaces() {
        List<Race> races = raceRepository.findAll();
        return races.stream().map(raceMapper::RaceToRaceDto).toList();
    }
    @Override
    public RaceDto getRaceById(Long raceId) {
        Optional<Race> race = raceRepository.findById(raceId);
        if (race.isEmpty()) {
            throw new RaceNotFoundException("Race not found!");
        }
        return raceMapper.RaceToRaceDto(race.get());
    }

    @Override
    public void validateRaceDoesNotExist(String name) {
        Optional<Race> race = raceRepository.findRaceByName(name);
        if (race.isPresent()) {
            throw new RaceAlreadyExistsException(String.format("Race with name %s already exists!", name));
        }
    }
}
