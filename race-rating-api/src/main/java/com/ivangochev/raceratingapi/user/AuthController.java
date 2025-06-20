package com.ivangochev.raceratingapi.user;

import com.ivangochev.raceratingapi.exception.DuplicatedUserInfoException;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.security.jwt.RefreshToken;
import com.ivangochev.raceratingapi.security.jwt.RefreshTokenService;
import com.ivangochev.raceratingapi.user.dto.*;
import com.ivangochev.raceratingapi.security.TokenProvider;
import com.ivangochev.raceratingapi.security.WebSecurityConfig;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import com.ivangochev.raceratingapi.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/cookies")
    public ResponseEntity<UserDto> getCookies(@RequestBody Map<String, String> tokens, HttpServletResponse response) {
        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");
        Jws<Claims> jws = tokenProvider.getJwtsClaims(accessToken).orElseThrow(() -> new AuthenticationCredentialsNotFoundException("JWT token is invalid"));
        response.addCookie(CookieUtils.generateCookie(CookieUtils.ACCESS_TOKEN, accessToken));
        response.addCookie(CookieUtils.generateCookie(CookieUtils.REFRESH_TOKEN, refreshToken));
        User user = userService.getUserByUsername(jws.getBody().getSubject()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("User {} logged in", user.getUsername());
        long tokenExpiresAt = tokenProvider.getTokenExpirationTimestamp(Boolean.FALSE);
        response.addHeader("Access-Token-Expires-At", String.valueOf(tokenExpiresAt));
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }


    @PostMapping("/authenticate")
    // Deprecated?
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authenticateAndGetToken(loginRequest.getUsername(), loginRequest.getPassword(), Boolean.FALSE);
        return new AuthResponse(token);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        if (userService.hasUserWithUsername(signUpRequest.getUsername())) {
            log.warn("User already exists: {}", signUpRequest.getUsername());
            throw new DuplicatedUserInfoException(String.format("Username %s already been used", signUpRequest.getUsername()));
        }
        if (userService.hasUserWithEmail(signUpRequest.getEmail())) {
            log.warn("Email already exists: {}", signUpRequest.getEmail());
            throw new DuplicatedUserInfoException(String.format("Email %s already been used", signUpRequest.getEmail()));
        }

        User user = userService.saveUser(mapSignUpRequestToUser(signUpRequest));
        String jwtToken = authenticateAndGetToken(signUpRequest.getUsername(), signUpRequest.getPassword(), Boolean.FALSE);

        // Add token expiration time
        long tokenExpiresAt = tokenProvider.getTokenExpirationTimestamp(Boolean.FALSE);
        response.addHeader("Access-Token-Expires-At", String.valueOf(tokenExpiresAt));

        UserDto userDto = userMapper.toUserDto(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getUsername());
        response.addCookie(CookieUtils.generateCookie(CookieUtils.ACCESS_TOKEN, jwtToken));
        response.addCookie(CookieUtils.generateCookie(CookieUtils.REFRESH_TOKEN, newRefreshToken.getToken()));
        return ResponseEntity.ok(userDto);
    }


    @PostMapping("/signin")
    public ResponseEntity<UserDto> signIn(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        User user = userService.getUserByEmail(signInRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Authentication failed for user: {}", signInRequest.getEmail());
                    return new UsernameNotFoundException("Invalid credentials");
                });

        String jwtToken;
        RefreshToken refreshToken;
        try {
            jwtToken = authenticateAndGetToken(user.getUsername(), signInRequest.getPassword(), Boolean.FALSE);
            refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            // Add token expiration time
            long tokenExpiresAt = tokenProvider.getTokenExpirationTimestamp(Boolean.FALSE);
            response.addHeader("Access-Token-Expires-At", String.valueOf(tokenExpiresAt));

            UserDto userDto = userMapper.toUserDto(user);
            response.addCookie(CookieUtils.generateCookie(CookieUtils.ACCESS_TOKEN, jwtToken));
            response.addCookie(CookieUtils.generateCookie(CookieUtils.REFRESH_TOKEN, refreshToken.getToken()));
            return ResponseEntity.ok(userDto);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", signInRequest.getEmail());
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieFromRequest(request, "refreshToken");

        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshToken);
        if (refreshTokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token expired. Please log in.");
        }
        refreshTokenService.verifyExpiration(refreshTokenOptional.get());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(currentUser.getUsername());
        String accessToken = tokenProvider.generate(currentUser, Boolean.FALSE);
        long tokenExpiresAt = tokenProvider.getTokenExpirationTimestamp(Boolean.FALSE);

        response.addHeader("Access-Token-Expires-At", String.valueOf(tokenExpiresAt));
        response.addCookie(CookieUtils.generateCookie(CookieUtils.ACCESS_TOKEN, accessToken));
        response.addCookie(CookieUtils.generateCookie(CookieUtils.REFRESH_TOKEN, newRefreshToken.getToken()));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @CookieValue(CookieUtils.ACCESS_TOKEN) String accessToken,
            @CookieValue(CookieUtils.REFRESH_TOKEN) String refreshToken,
            HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(refreshToken);
        tokenProvider.getJwtsClaims(accessToken).ifPresent(claims -> {
            claims.getBody().setExpiration(new java.util.Date(0));
            response.addCookie(CookieUtils.generateCookie(CookieUtils.ACCESS_TOKEN, null, 0));
            response.addCookie(CookieUtils.generateCookie(CookieUtils.REFRESH_TOKEN, null, 0));
            log.info("User {} logged out", claims.getBody().getSubject());
        });
        return ResponseEntity.noContent().build();
    }


    private String authenticateAndGetToken(String username, String password, Boolean rememberMe) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return tokenProvider.generate(customUserDetails, rememberMe);
    }

    private User mapSignUpRequestToUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setRole(WebSecurityConfig.USER);
        user.setProvider(OAuth2Provider.LOCAL);
        return user;
    }
}