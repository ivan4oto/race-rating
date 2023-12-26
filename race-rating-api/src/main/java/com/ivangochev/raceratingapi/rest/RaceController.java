package com.ivangochev.raceratingapi.rest;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.service.RaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/races")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Race>> getAllRaces() {
        List<Race> allRaces = raceService.getAllRaces();
        return new ResponseEntity<>(allRaces, HttpStatus.OK);
    }
}
