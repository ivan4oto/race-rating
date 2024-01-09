package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.user.dto.UserDto;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;
    private final RaceRepository raceRepository;

    @GetMapping("/ratings/race/{raceId}")
    public ResponseEntity<List<Rating>> getRatings(@PathVariable Long raceId) {
        Optional<Race> race = raceRepository.findById(raceId);
        if (race.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Rating> ratings = ratingService.findByRace(race.get());
        return ResponseEntity.ok(ratings);
    }

    @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/ratings")
    public ResponseEntity<Rating> createRating(@AuthenticationPrincipal CustomUserDetails currentUser,
                                               @Valid @RequestBody RatingDto ratingDto) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        Rating savedRating = ratingService.saveRating(ratingDto, user);
        return new ResponseEntity<>(savedRating, HttpStatus.CREATED);
    }

    @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
    @DeleteMapping("/ratings/{id}")
    public void deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
    }

    @RequiredArgsConstructor
    @RestController
    @RequestMapping("/api/users")
    public static class UserController {

        private final UserService userService;
        private final UserMapper userMapper;

        @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
        @GetMapping("/me")
        public UserDto getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
            User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
            return userMapper.toUserDto(user);
        }

        @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
        @GetMapping
        public List<UserDto> getUsers() {
            return userService.getUsers().stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
        @GetMapping("/{username}")
        public UserDto getUser(@PathVariable String username) {
            return userMapper.toUserDto(userService.validateAndGetUserByUsername(username));
        }

        @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
        @DeleteMapping("/{username}")
        public UserDto deleteUser(@PathVariable String username) {
            User user = userService.validateAndGetUserByUsername(username);
            userService.deleteUser(user);
            return userMapper.toUserDto(user);
        }
    }
}
