package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.exception.RaceAlreadyExistsException;
import com.ivangochev.raceratingapi.logo.LogoProcessor;
import com.ivangochev.raceratingapi.notification.event.RaceCreatedEvent;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceSummaryDto;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.config.AwsProperties;
import com.ivangochev.raceratingapi.aws.S3UploadFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RaceServiceImpl implements RaceService {
    private final ApplicationEventPublisher eventPublisher;
    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;
    private final S3UploadFileService s3UploadFileService;
    @Value("${aws.s3.root-folder}")
    private String s3RootFolder;
    private final AwsProperties awsProperties;
    private final LogoProcessor logoProcessor;

    @Override
    @Transactional
    public RaceDto createRace(CreateRaceDto raceDto, User user) {
        log.info("Creating race {}", raceDto);
        Race raceToCreate = raceMapper.createRaceDtoToRace(raceDto, user);
        Race saved = raceRepository.save(raceToCreate);
        eventPublisher.publishEvent(new RaceCreatedEvent(
                this, saved.getId(), user.getId(), saved.getName()));
        return raceMapper.RaceToRaceDto(saved);
    }

    @Override
    public RaceDto editRace(Long raceId, CreateRaceDto raceDto) {
        Race race = raceRepository.findById(raceId).orElseThrow(
                () -> new RaceNotFoundException("Race not found!"));
        Race updatedRace = raceRepository.save(raceMapper.editRace(raceDto, race));
        return raceMapper.RaceToRaceDto(updatedRace);
    }

    @Override
    @Transactional
    public RaceDto updateRaceLogo(Long raceId, MultipartFile logoFile) {
        log.info("Updating race logo for race with id {}", raceId);
        Race race = raceRepository.findById(raceId).orElseThrow(
                () -> new RaceNotFoundException("Race not found!"));
        try {
            String extension = StringUtils.getFilenameExtension(logoFile.getOriginalFilename());
            String safeExtension = (extension != null && !extension.isBlank()) ? extension : "png";
            String key = String.format("%s/race-logos/original/%d/logo.%s", s3RootFolder, raceId, safeExtension);

            byte[] bytes = logoFile.getBytes();
            s3UploadFileService.uploadFile(key, bytes, logoFile.getContentType());

            String logoUrl = String.format("https://%s.s3.amazonaws.com/%s", awsProperties.getBucketName(), key);
            race.setLogoUrl(logoUrl);
            Race saved = raceRepository.save(race);
            logoProcessor.processAndUploadLogoAsync(saved.getLogoUrl(), saved.getId());
            return raceMapper.RaceToRaceDto(saved);
        } catch (Exception e) {
            log.error("Error updating race logo for id {}", raceId, e);
            throw new RuntimeException("Failed to update race logo", e);
        }
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

    @Override
    public boolean hasUserCommented(Long raceId, Long userId) {
        return raceRepository.existsByIdAndCommenters_Id(raceId, userId);
    }
}
