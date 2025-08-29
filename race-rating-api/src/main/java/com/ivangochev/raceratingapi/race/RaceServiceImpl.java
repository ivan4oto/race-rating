package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.events.RaceCreatedEvent;
import com.ivangochev.raceratingapi.exception.RaceAlreadyExistsException;
import com.ivangochev.raceratingapi.notification.NotificationService;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceSummaryDto;
import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RaceServiceImpl implements RaceService {
    private final ApplicationEventPublisher publisher;
    private final NotificationService notificationService;
    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;

    @Override
    @Transactional
    public RaceDto createRace(CreateRaceDto raceDto, User user) {
        log.info("Creating race {}", raceDto);
        Race raceToCreate = raceMapper.createRaceDtoToRace(raceDto, user);
        Race saved = raceRepository.save(raceToCreate);
        notificationService.notifyAllUsersAboutNewRace(saved.getId(), saved.getName());
        return raceMapper.RaceToRaceDto(saved);
    }

    @Override
    public RaceDto editRace(Long raceId, CreateRaceDto raceDto) {
        Race race = raceRepository.findById(raceId).orElseThrow(
                () -> new RaceNotFoundException("Race not found!"));
        Race updatedRace = raceRepository.save(raceMapper.editRace(raceDto, race));
        return raceMapper.RaceToRaceDto(updatedRace);
    }

    public void saveAllRaces(List<Race> races) {
        raceRepository.saveAll(races);
    }

    @Override
    public List<RaceSummaryDto> getAllRaces() {
        return raceRepository.findAllRaceSummaries();
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

    @Override
    public boolean isRaceOwner(Long raceId, User user) {
        Optional<Race> race = raceRepository.findById(raceId);
        return race.map(value -> value.getAuthor().equals(user)).orElse(false);
    }

    @Override
    public void deleteRace(Long raceId) {
        log.info("Deleting race with id {}", raceId);
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new EntityNotFoundException("Race not found: " + raceId));

        race.getCommenters().forEach(u -> u.getCommentedForRaces().remove(race));
        race.getVoters().forEach(u -> u.getVotedForRaces().remove(race));

        raceRepository.delete(race);
        log.info("Race with id {} deleted", raceId);
    }
}
