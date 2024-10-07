package com.ivangochev.raceratingapi.user;

import com.ivangochev.raceratingapi.exception.DuplicatedUserInfoException;
import com.ivangochev.raceratingapi.user.dto.AuthResponse;
import com.ivangochev.raceratingapi.user.dto.LoginRequest;
import com.ivangochev.raceratingapi.user.dto.SignInRequest;
import com.ivangochev.raceratingapi.user.dto.SignUpRequest;
import com.ivangochev.raceratingapi.security.TokenProvider;
import com.ivangochev.raceratingapi.security.WebSecurityConfig;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("/authenticate")
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authenticateAndGetToken(loginRequest.getUsername(), loginRequest.getPassword());
        return new AuthResponse(token);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.hasUserWithUsername(signUpRequest.getUsername())) {
            throw new DuplicatedUserInfoException(String.format("Username %s already been used", signUpRequest.getUsername()));
        }
        if (userService.hasUserWithEmail(signUpRequest.getEmail())) {
            throw new DuplicatedUserInfoException(String.format("Email %s already been used", signUpRequest.getEmail()));
        }
        userService.saveUser(mapSignUpRequestToUser(signUpRequest));
        String token = authenticateAndGetToken(signUpRequest.getUsername(), signUpRequest.getPassword());
        // Maybe return AuthResponse with token instead of String?
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        User user = userService.getUserByEmail(signInRequest.getEmail())
                .orElseThrow(() ->
                        new AuthenticationCredentialsNotFoundException(
                                String.format("Authentication error. Email %s not found.", signInRequest.getEmail())
                        )
                );
        String token;
        try {
            token = authenticateAndGetToken(user.getUsername(), signInRequest.getPassword());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid credentials");
        }
        // Maybe return AuthResponse with token instead of String?
        return ResponseEntity.ok(token);
    }

    private String authenticateAndGetToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return tokenProvider.generate(authentication);
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
