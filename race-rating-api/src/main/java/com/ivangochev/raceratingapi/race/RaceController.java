package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.config.AwsProperties;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.utils.aws.S3PresignedUrlGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RaceController {
    private final RaceService raceService;
    private final UserService userService;
    private final AwsProperties awsProperties;
    private final S3PresignedUrlGenerator s3PresignedUrlGenerator;

    public RaceController(RaceService raceService, UserService userService, AwsProperties awsProperties, S3PresignedUrlGenerator s3PresignedUrlGenerator) {
        this.raceService = raceService;
        this.userService = userService;
        this.awsProperties = awsProperties;
        this.s3PresignedUrlGenerator = s3PresignedUrlGenerator;
    }

    @GetMapping("/race/all")
    public ResponseEntity<List<RaceDto>> getAllRaces() {
        List<RaceDto> allRaces = raceService.getAllRaces();
        return new ResponseEntity<>(allRaces, HttpStatus.OK);
    }
    @GetMapping("/race/{raceId}")
    public ResponseEntity<Race> getRatings(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long raceId) {
        if (currentUser != null) {
            User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        }
        Optional<Race> race = raceService.getRaceById(raceId);
        if (race.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Race foundRace = race.get();
        return ResponseEntity.ok(foundRace);
    }

    @PostMapping("/race")
    public ResponseEntity<Race> createRace(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody CreateRaceDto raceDto) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        raceService.validateRaceDoesNotExist(raceDto.name());
        Race race = raceService.createRace(raceDto, user);
        return new ResponseEntity<Race>(race, HttpStatus.CREATED);
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<URL> getPresignedUrl(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        URL presignedUrl = s3PresignedUrlGenerator.generatePresignedUrl(
            awsProperties.getBucketName(),
            "test.txt",
            5
        );
        return new ResponseEntity<>(presignedUrl, HttpStatus.OK);
    }

}
