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
        newRace.setRatingsCount(0);
        newRace.setName(raceDto.name());
        newRace.setDescription(raceDto.description());
        newRace.setAverageRating(BigDecimal.valueOf(0));
        newRace.setAverageTraceScore(BigDecimal.valueOf(0));
        newRace.setAverageVibeScore(BigDecimal.valueOf(0));
        newRace.setAverageOrganizationScore(BigDecimal.valueOf(0));
        newRace.setAverageLocationScore(BigDecimal.valueOf(0));
        newRace.setAverageValueScore(BigDecimal.valueOf(0));
        newRace.setRatingsCount(0);
        newRace.setLatitude(raceDto.latitude());
        newRace.setLongitude(raceDto.longitude());
        newRace.setWebsiteUrl(raceDto.websiteUrl());
        newRace.setLogoUrl(raceDto.logoUrl());
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
        newRaceDto.setRatingsCount(race.getRatingsCount());
        newRaceDto.setLatitude(race.getLatitude());
        newRaceDto.setLongitude(race.getLongitude());
        newRaceDto.setWebsiteUrl(race.getWebsiteUrl());
        newRaceDto.setLogoUrl(race.getLogoUrl());
        newRaceDto.setEventDate(race.getEventDate());
        newRaceDto.setAuthorId(getAuthorIdForRace(race));
        return newRaceDto;
    }

    public Race editRace(CreateRaceDto raceDto, Race race) {
        race.setName(raceDto.name());
        race.setDescription(raceDto.description());
        race.setLatitude(raceDto.latitude());
        race.setLongitude(raceDto.longitude());
        race.setWebsiteUrl(raceDto.websiteUrl());
        race.setLogoUrl(raceDto.logoUrl());
        race.setEventDate(raceDto.eventDate());
        return race;
    }

    private Long getAuthorIdForRace(Race race) {
        if (race.getAuthor() != null) {
            return race.getAuthor().getId();
        }
        return null;
    }
}
