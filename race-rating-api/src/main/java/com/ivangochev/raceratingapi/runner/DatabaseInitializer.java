package com.ivangochev.raceratingapi.runner;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.RaceComment;
import com.ivangochev.raceratingapi.rating.Rating;
import com.ivangochev.raceratingapi.model.User;
import com.ivangochev.raceratingapi.repository.RaceCommentRepository;
import com.ivangochev.raceratingapi.security.WebSecurityConfig;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import com.ivangochev.raceratingapi.service.RaceService;
import com.ivangochev.raceratingapi.rating.RatingService;
import com.ivangochev.raceratingapi.service.UserService;
import com.ivangochev.raceratingapi.utils.JsonUtils;
import com.ivangochev.raceratingapi.utils.ResourceFileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final JsonUtils jsonUtils;
    private final UserService userService;
    private final RatingService ratingService;
    private final RaceService raceService;
    private final RaceCommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userService.getUsers().isEmpty()) {
            return;
        }
        USERS.forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
        });
        List<Race> races;
        List<Rating> ratings;
        List<RaceComment> comments;

        try {
            String racesJson = ResourceFileReader.readJsonFileFromClasspath("races.json");
            races = jsonUtils.fromJsonToRaces(racesJson);
            raceService.saveAllRaces(races);

            String ratingsJson = ResourceFileReader.readJsonFileFromClasspath("ratings.json");
            ratings = jsonUtils.fromJsonToRatings(ratingsJson);
            ratingService.saveAllRatings(ratings);

            String commentsJson = ResourceFileReader.readJsonFileFromClasspath("comments.json");
            comments = jsonUtils.fromJsonToComments(commentsJson);
            commentRepository.saveAll(comments);
        } catch (IOException e) {
            log.error("Unable to load database objects from json!");
            e.printStackTrace();
        }


        log.info("Database initialized");
    }


    private static final List<User> USERS = Arrays.asList(
            new User("admin", "admin", "Admin", "admin@mycompany.com", WebSecurityConfig.ADMIN, null, OAuth2Provider.LOCAL, "1"),
            new User("user", "user", "User", "user@mycompany.com", WebSecurityConfig.USER, null, OAuth2Provider.LOCAL, "2"),
            new User("rosros", "rosros", "Rosen Rusev", "rusev@gmail.com", WebSecurityConfig.USER, "https://static-00.iconduck.com/assets.00/user-avatar-1-icon-2048x2048-935gruik.png", OAuth2Provider.LOCAL, "2"),
            new User("tonton", "tonton", "Toni Petkov", "tonton@gmail.com", WebSecurityConfig.USER, "https://static-00.iconduck.com/assets.00/user-avatar-1-icon-2048x2048-935gruik.png", OAuth2Provider.LOCAL, "2")
    );

}
