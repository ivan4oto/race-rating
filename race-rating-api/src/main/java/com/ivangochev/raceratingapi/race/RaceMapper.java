package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class RaceMapper {
    Race createRaceDtoToRace(CreateRaceDto raceDto, User user) {
        Race newRace = new Race();
        newRace.setName(raceDto.name());
        newRace.setDescription(raceDto.description());
        newRace.setAverageRating(BigDecimal.valueOf(5.00));
        newRace.setAverageTraceScore(BigDecimal.valueOf(5.00));
        newRace.setAverageVibeScore(BigDecimal.valueOf(5.00));
        newRace.setAverageOrganizationScore(BigDecimal.valueOf(5.00));
        newRace.setAverageLocationScore(BigDecimal.valueOf(5.00));
        newRace.setAverageValueScore(BigDecimal.valueOf(5.00));
        newRace.setNumberOfVoters(1);
        newRace.setLatitude(raceDto.latitude());
        newRace.setLongitude(raceDto.longitude());
        newRace.setWebsiteUrl(raceDto.websiteUrl());
        newRace.setLogoUrl(raceDto.logoUrl());
        newRace.setTerrain(raceDto.terrain());
        newRace.setDistance(raceDto.distance());
        newRace.setElevation(raceDto.elevation());
        newRace.setEventDate(raceDto.eventDate());

        newRace.setCreatedBy(user);

        return newRace;
    }
}
