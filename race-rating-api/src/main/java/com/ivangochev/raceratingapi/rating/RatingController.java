package com.ivangochev.raceratingapi.rating;

import com.ivangochev.raceratingapi.security.TokenProvider;
import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.user.dto.UserDto;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.config.SwaggerConfig;
import com.ivangochev.raceratingapi.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public ResponseEntity<List<RatingDto>> getRatings(@PathVariable Long raceId) {
        Optional<Race> race = raceRepository.findById(raceId);
        if (race.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<RatingDto> ratings = ratingService.findByRace(race.get());
        return ResponseEntity.ok(ratings);
    }

    @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/ratings")
    public ResponseEntity<RatingDto> createRating(@AuthenticationPrincipal CustomUserDetails currentUser,
                                               @Valid @RequestBody RatingDto ratingDto) {
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        RatingDto savedRating = ratingService.saveRating(ratingDto, user);
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
        private final TokenProvider tokenProvider;

        @Operation(security = {@SecurityRequirement(name = SwaggerConfig.BEARER_KEY_SECURITY_SCHEME)})
        @GetMapping("/me")
        public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser,
                                      @CookieValue(CookieUtils.ACCESS_TOKEN) String accessToken,
                                      HttpServletResponse response) {
            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
            Optional<Jws<Claims>> claims = tokenProvider.getJwtsClaims(accessToken);
            if (claims.isEmpty()) {
                throw new IllegalArgumentException("Invalid access token");
            }
            Date accessTokenExpTime = claims.get().getBody().getExpiration();
            response.addHeader("Access-Token-Expires-At", String.valueOf(accessTokenExpTime.getTime() / 1000));

            return new ResponseEntity<>(userMapper.toUserDto(user), HttpStatus.OK);
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
