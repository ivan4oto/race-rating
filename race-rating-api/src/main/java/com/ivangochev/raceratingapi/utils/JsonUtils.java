package com.ivangochev.raceratingapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.racecomment.RaceCommentDTO;
import com.ivangochev.raceratingapi.rating.Rating;
import com.ivangochev.raceratingapi.rating.RatingDto;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonUtils {
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;

    public JsonUtils(ObjectMapper mapper, UserRepository userRepository, RaceRepository raceRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.raceRepository = raceRepository;
    }

    public List<Race> fromJsonToRaces(String json) throws IOException {
        List<RaceDto> raceDtos = mapper.readValue(json, new TypeReference<List<RaceDto>>() {});

        List<Race> races = new ArrayList<>();
        for (RaceDto dto : raceDtos) {
            Race race = new Race();
            race.setId(dto.getId());
            race.setName(dto.getName());
            race.setDescription(dto.getDescription());
            race.setAverageRating(dto.getAverageRating());
            race.setAverageOrganizationScore(dto.getAverageOrganizationScore());
            race.setAverageLocationScore(dto.getAverageLocationScore());
            race.setAverageVibeScore(dto.getAverageVibeScore());
            race.setAverageTraceScore(dto.getAverageTraceScore());
            race.setAverageValueScore(dto.getAverageValueScore());
            race.setLatitude(dto.getLatitude());
            race.setLongitude(dto.getLongitude());
            race.setWebsiteUrl(dto.getWebsiteUrl());
            race.setLogoUrl(dto.getLogoUrl());
            race.setTerrainTags(dto.getTerrainTags());
            race.setDistance(dto.getDistance());
            race.setElevation(dto.getElevation());
            race.setEventDate(dto.getEventDate());

            if (dto.getAuthorId() != null) {
                User user = userRepository.findById(dto.getAuthorId()).orElse(null);
                race.setAuthor(user);
            }
            races.add(race);
        }
        return races;
    }

    public List<Rating> fromJsonToRatings(String json) throws IOException {
        List<RatingDto> ratingDtos = mapper.readValue(json, new TypeReference<List<RatingDto>>() {});

        List<Rating> ratings = new ArrayList<>();
        for (RatingDto dto : ratingDtos) {
            Rating rating = new Rating();
            rating.setId(dto.getId());
            rating.setTraceScore(dto.getTraceScore());
            rating.setVibeScore(dto.getVibeScore());
            rating.setOrganizationScore(dto.getOrganizationScore());
            rating.setLocationScore(dto.getOrganizationScore());
            rating.setValueScore(dto.getValueScore());
            rating.setCreatedAt(dto.getCreatedAt());

            if (dto.getAuthorId() != null) {
                User user = userRepository.findById(dto.getAuthorId()).orElse(null);
                rating.setAuthor(user);
            }

            if (dto.getRaceId() != null) {
                Race race = raceRepository.findById(dto.getRaceId()).orElse(null);
                rating.setRace(race);
            }
            ratings.add(rating);
        }
        return ratings;
    }

    public List<RaceComment> fromJsonToComments(String json) throws JsonProcessingException {
        List<RaceCommentDTO> commentDTOS = mapper.readValue(json, new TypeReference<List<RaceCommentDTO>>() {
        });

        List<RaceComment> comments = new ArrayList<>();
        for (RaceCommentDTO dto : commentDTOS) {
            RaceComment comment = new RaceComment();
            comment.setId(dto.getId());
            comment.setRaceId(dto.getRaceId());
            comment.setCreatedAt(dto.getCreatedAt());
            comment.setCommentText(dto.getCommentText());
            if (dto.getAuthorId() != null) {
                User user = userRepository.findById(dto.getAuthorId()).orElse(null);
                comment.setAuthor(user);
            }
            comments.add(comment);
        }

        return comments;
    }

    public List<User> fromJsonToUsers(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<>() {
        });
    }
}


