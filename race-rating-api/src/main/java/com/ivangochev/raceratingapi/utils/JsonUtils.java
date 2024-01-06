package com.ivangochev.raceratingapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.RaceComment;
import com.ivangochev.raceratingapi.repository.RaceRepository;
import com.ivangochev.raceratingapi.rest.dto.RaceCommentDTO;
import com.ivangochev.raceratingapi.rest.dto.RaceDTO;
import com.ivangochev.raceratingapi.model.Rating;
import com.ivangochev.raceratingapi.model.User;
import com.ivangochev.raceratingapi.repository.UserRepository;
import com.ivangochev.raceratingapi.rest.dto.RatingDTO;
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
        List<RaceDTO> raceDtos = mapper.readValue(json, new TypeReference<List<RaceDTO>>() {});

        List<Race> races = new ArrayList<>();
        for (RaceDTO dto : raceDtos) {
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
            race.setTerrain(dto.getTerrain());
            race.setDistance(dto.getDistance());
            race.setElevation(dto.getElevation());
            race.setEventDate(dto.getEventDate());

            if (dto.getCreatedById() != null) {
                User user = userRepository.findById(dto.getCreatedById()).orElse(null);
                race.setCreatedBy(user);
            }
            races.add(race);
        }
        return races;
    }

    public List<Rating> fromJsonToRatings(String json) throws IOException {
        List<RatingDTO> ratingDTOS = mapper.readValue(json, new TypeReference<List<RatingDTO>>() {});

        List<Rating> ratings = new ArrayList<>();
        for (RatingDTO dto : ratingDTOS) {
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
}


