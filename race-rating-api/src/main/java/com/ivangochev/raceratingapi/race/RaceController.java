package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.config.AwsProperties;
import com.ivangochev.raceratingapi.logo.LogoProcessor;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceSummaryDto;
import com.ivangochev.raceratingapi.race.dto.S3ObjectDto;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.utils.aws.S3PresignedUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class RaceController {
    private final RaceService raceService;
    private final UserService userService;
    private final AwsProperties awsProperties;
    private final S3PresignedUrlGenerator s3PresignedUrlGenerator;
    private final S3Client s3Client;
    private final S3ObjectMapper s3ObjectMapper;
    private final LogoProcessor logoProcessor;

    public RaceController(RaceService raceService, UserService userService, AwsProperties awsProperties, S3PresignedUrlGenerator s3PresignedUrlGenerator, S3Client s3Client, S3ObjectMapper s3ObjectMapper, LogoProcessor logoProcessor) {
        this.raceService = raceService;
        this.userService = userService;
        this.awsProperties = awsProperties;
        this.s3PresignedUrlGenerator = s3PresignedUrlGenerator;
        this.s3Client = s3Client;
        this.s3ObjectMapper = s3ObjectMapper;
        this.logoProcessor = logoProcessor;
    }

    @GetMapping("/race/all")
    public ResponseEntity<List<RaceSummaryDto>> getAllRaces() {
        List<RaceSummaryDto> allRaces = raceService.getAllRaces();
        return new ResponseEntity<>(allRaces, HttpStatus.OK);
    }
    @GetMapping("/race/{raceId}")
    public ResponseEntity<RaceDto> getRace(
            @PathVariable Long raceId) {
        RaceDto race = raceService.getRaceById(raceId);
        return ResponseEntity.ok(race);
    }

    @PostMapping("/race")
    public ResponseEntity<Race> createRace(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody CreateRaceDto raceDto) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        raceService.validateRaceDoesNotExist(raceDto.name());
        Race race = raceService.createRace(raceDto, user);
        logoProcessor.processAndUploadLogoAsync(race.getLogoUrl(), race.getId());
        return new ResponseEntity<>(race, HttpStatus.CREATED);
    }

    @PutMapping("/race/{raceId}")
    public ResponseEntity<RaceDto> updateRace(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody CreateRaceDto raceDto,
            @PathVariable Long raceId) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        if (!raceService.isRaceOwner(raceId, user) && !user.isAdmin()) {
            log.error("User {} is not owner of race {}", user.getUsername(), raceId);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        RaceDto editedRace = raceService.editRace(raceId, raceDto);
        return new ResponseEntity<>(editedRace, HttpStatus.OK);
    }

    @DeleteMapping("/race/{raceId}")
    public ResponseEntity<Void> deleteRace(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long raceId
    ) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        if (!user.isAdmin()) {
            log.warn("User {} is not admin. Attempt to delete race id: {}", user.getUsername(), raceId);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        raceService.deleteRace(raceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PostMapping("/get-presigned-urls")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestBody Map<String, List<String>> requestBody
    ) {
        List<String> objectKeys = requestBody.get("objectKeys");
        Map<String, String> presignedUrls = new HashMap<>();
        for (String objectKey : objectKeys) {
            String presignedUrl = s3PresignedUrlGenerator.generatePresignedUrl(
                    awsProperties.getBucketName(),
                    objectKey,
                    5
            ).toString();
            presignedUrls.put(objectKey, presignedUrl);
        }
        return new ResponseEntity<>(presignedUrls, HttpStatus.OK);
    }

    @GetMapping("/race/{raceId}/list-images")
    public ResponseEntity<List<S3ObjectDto>> listObjects(@PathVariable Long raceId) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket("race-rating")
                .prefix("resources/images/" + raceId + "/")
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(request);
        List<S3ObjectDto> s3ObjectDtos = result.contents().stream()
                .filter(s3Object -> s3Object.size() > 0)
                .map(s3Object -> s3ObjectMapper.map(s3Object, result.name()))
                .toList();
        return new ResponseEntity<>(s3ObjectDtos, HttpStatus.OK);
    }

}
