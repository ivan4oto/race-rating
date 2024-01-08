package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.model.User;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public")
public class RaceController {
    private final RaceService raceService;
    private final UserService userService;

    public RaceController(RaceService raceService, UserService userService) {
        this.raceService = raceService;
        this.userService = userService;
    }

    @GetMapping("/races/all")
    public ResponseEntity<List<Race>> getAllRaces() {
        List<Race> allRaces = raceService.getAllRaces();
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
}
