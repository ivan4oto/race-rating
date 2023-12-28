package com.ivangochev.raceratingapi.rest;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.Rating;
import com.ivangochev.raceratingapi.service.RaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping("/races/all")
    public ResponseEntity<List<Race>> getAllRaces() {
        List<Race> allRaces = raceService.getAllRaces();
        return new ResponseEntity<>(allRaces, HttpStatus.OK);
    }
    @GetMapping("/race/{raceId}")
    public ResponseEntity<Race> getRatings(@PathVariable Long raceId) {
        Optional<Race> race = raceService.getRaceById(raceId);
        if (race.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Race foundRace = race.get();
        return ResponseEntity.ok(foundRace);
    }
}
