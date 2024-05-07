package com.ivangochev.raceratingapi.runner;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.rating.Rating;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.racecomment.RaceCommentRepository;
import com.ivangochev.raceratingapi.race.RaceService;
import com.ivangochev.raceratingapi.rating.RatingService;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.utils.JsonUtils;
import com.ivangochev.raceratingapi.utils.ResourceFileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
@Profile("dev")
public class DatabaseInitializer implements CommandLineRunner {

    private final JsonUtils jsonUtils;
    private final UserService userService;
    private final RatingService ratingService;
    private final RaceService raceService;
    private final RaceCommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<Race> races;
        List<Rating> ratings;
        List<RaceComment> comments;

//        try {
//            String usersJson = ResourceFileReader.readJsonFileFromClasspath("users.json");
//            List<User> users = jsonUtils.fromJsonToUsers(usersJson);
//            users.forEach(user -> {
//                user.setPassword(passwordEncoder.encode(user.getPassword()));
//                userService.saveUser(user);
//            });
//
//            String racesJson = ResourceFileReader.readJsonFileFromClasspath("races.json");
//            races = jsonUtils.fromJsonToRaces(racesJson);
//            raceService.saveAllRaces(races);
//
//            String ratingsJson = ResourceFileReader.readJsonFileFromClasspath("ratings.json");
//            ratings = jsonUtils.fromJsonToRatings(ratingsJson);
//            ratingService.saveAllRatings(ratings);
//
//            String commentsJson = ResourceFileReader.readJsonFileFromClasspath("comments.json");
//            comments = jsonUtils.fromJsonToComments(commentsJson);
//            commentRepository.saveAll(comments);
//        } catch (IOException e) {
//            log.error("Unable to load database objects from json!");
//            e.printStackTrace();
//        }

        log.info("Database initialized");
    }

}
