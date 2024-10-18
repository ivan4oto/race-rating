package com.ivangochev.raceratingapi.user;

import com.ivangochev.raceratingapi.exception.DuplicatedUserInfoException;
import com.ivangochev.raceratingapi.user.dto.*;
import com.ivangochev.raceratingapi.security.TokenProvider;
import com.ivangochev.raceratingapi.security.WebSecurityConfig;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.ivangochev.raceratingapi.security.TokenAuthenticationFilter.JWT_COOKIE_NAME;


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

    @PostMapping("/authenticate")
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
        UserDto userDto = userMapper.toUserDto(user);
        response.addCookie(createJwtCookie(jwtToken));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserDto> signIn(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        User user = userService.getUserByEmail(signInRequest.getEmail())
                .orElseThrow(() -> {
                            log.warn("Authentication failed for user: {}", signInRequest.getEmail());
                            return new UsernameNotFoundException(
                                    String.format("Authentication error. Email %s not found.", signInRequest.getEmail())
                            );
                        }
                );
        String jwtToken;
        try {
            jwtToken = authenticateAndGetToken(user.getUsername(), signInRequest.getPassword(), Boolean.FALSE);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", signInRequest.getEmail());
            throw new BadCredentialsException("Invalid credentials");
        }
        UserDto userDto = userMapper.toUserDto(user);
        response.addCookie(createJwtCookie(jwtToken));
        return ResponseEntity.ok(userDto);
    }

    private String authenticateAndGetToken(String username, String password, Boolean rememberMe) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return tokenProvider.generate(authentication, rememberMe);
    }

    private Cookie createJwtCookie(String jwtToken) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, jwtToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setSecure(Boolean.TRUE);
        return cookie;
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
