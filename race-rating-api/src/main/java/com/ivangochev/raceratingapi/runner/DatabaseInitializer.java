package com.ivangochev.raceratingapi.runner;

import com.ivangochev.raceratingapi.model.Race;
import com.ivangochev.raceratingapi.model.Rating;
import com.ivangochev.raceratingapi.model.User;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import com.ivangochev.raceratingapi.security.WebSecurityConfig;
import com.ivangochev.raceratingapi.service.RaceService;
import com.ivangochev.raceratingapi.service.RatingService;
import com.ivangochev.raceratingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RatingService ratingService;
    private final RaceService raceService;
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

        raceService.saveRace(BALKAN_ULTRA);

        RATINGS.forEach(ratingService::saveRating);
        log.info("Database initialized");
    }

    private static final List<User> USERS = Arrays.asList(
            new User("admin", "admin", "Admin", "admin@mycompany.com", WebSecurityConfig.ADMIN, null, OAuth2Provider.LOCAL, "1"),
            new User("user", "user", "User", "user@mycompany.com", WebSecurityConfig.USER, null, OAuth2Provider.LOCAL, "2")
    );

    private static final Race BALKAN_ULTRA = new Race(1L, "Balkan Ultra", BigDecimal.valueOf(42.749601), BigDecimal.valueOf(24.895403));
    private static final List<Rating> RATINGS = Arrays.asList(
            new Rating(1L, BALKAN_ULTRA, USERS.get(1), 5, 5, 5, 5, 5, Boolean.TRUE, "Basi Dobroto Bqgane!", null, new Date())
    );
}
