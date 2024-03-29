package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        newRace.setTerrainTags(raceDto.terrainTags());
        newRace.setDistance(raceDto.distance());
        newRace.setElevation(raceDto.elevation());
        newRace.setEventDate(raceDto.eventDate());

        newRace.setAuthor(user);

        return newRace;
    }

    RaceDto RaceToRaceDto(Race race) {
        RaceDto newRaceDto = new RaceDto();
        newRaceDto.setId(race.getId());
        newRaceDto.setName(race.getName());
        newRaceDto.setDescription(race.getDescription());
        newRaceDto.setAverageRating(race.getAverageRating());
        newRaceDto.setAverageTraceScore(race.getAverageTraceScore());
        newRaceDto.setAverageVibeScore(race.getAverageVibeScore());
        newRaceDto.setAverageOrganizationScore(race.getAverageOrganizationScore());
        newRaceDto.setAverageLocationScore(race.getAverageLocationScore());
        newRaceDto.setAverageValueScore(race.getAverageValueScore());
        newRaceDto.setLatitude(race.getLatitude());
        newRaceDto.setLongitude(race.getLongitude());
        newRaceDto.setWebsiteUrl(race.getWebsiteUrl());
        newRaceDto.setLogoUrl(race.getLogoUrl());
        newRaceDto.setTerrainTags(race.getTerrainTags());
        newRaceDto.setDistance(race.getDistance());
        newRaceDto.setElevation(race.getElevation());
        newRaceDto.setEventDate(race.getEventDate());
        newRaceDto.setAuthorId(getAuthorIdForRace(race));

        return newRaceDto;
    }

    private Long getAuthorIdForRace(Race race) {
        if (race.getAuthor() != null) {
            return race.getAuthor().getId();
        }
        return null;
    }
}
