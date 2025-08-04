package com.ivangochev.raceratingapi.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivangochev.raceratingapi.avatar.AvatarService;
import com.ivangochev.raceratingapi.config.CustomTestConfig;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.security.TokenProvider;
import com.ivangochev.raceratingapi.security.jwt.RefreshToken;
import com.ivangochev.raceratingapi.security.jwt.RefreshTokenService;
import com.ivangochev.raceratingapi.user.dto.SignInRequest;
import com.ivangochev.raceratingapi.user.dto.SignUpRequest;
import com.ivangochev.raceratingapi.user.dto.UserDto;
import com.ivangochev.raceratingapi.user.mapper.UserMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(CustomTestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AvatarService avatarService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenValidSignUpRequest_thenReturnsUserDtoWithCookiesAndHeader() throws Exception {
        // Given
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john123");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setEmail("john@example.com");

        User user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setEmail("john@example.com");

        UserDto userDto = new UserDto(
                1L,
                "john123",
                "John Doe",
                "john@example.com",
                "https://s3.example.com/avatar.png",
                "USER",
                Collections.emptyList(),
                Collections.emptyList()
        );

        String encodedPassword = "encoded-password";
        String jwtToken = "jwt-token";
        String refreshTokenString = "refresh-token";
        long expiresAt = 123456789L;

        // When
        when(userService.hasUserWithUsername("john123")).thenReturn(false);
        when(userService.hasUserWithEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userService.saveUser(any(User.class))).thenReturn(user); // Called twice
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        when(avatarService.getAvatarUrl(any())).thenReturn("https://s3.example.com/avatar.png");
        when(tokenProvider.generate(any(CustomUserDetails.class), eq(false))).thenReturn(jwtToken);
        when(tokenProvider.getTokenExpirationTimestamp(false)).thenReturn(expiresAt);
        RefreshToken refreshToken = RefreshToken.builder()
                .id(10L)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusSeconds(3600))
                .user(user)
                .build();
        when(refreshTokenService.createRefreshToken("john123"))
                .thenReturn(refreshToken);

        Authentication mockAuth = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(mockAuth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        // Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john123"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(cookie().value("accessToken", jwtToken))
                .andExpect(cookie().value("refreshToken", refreshToken.getToken()))
                .andExpect(header().string("Access-Token-Expires-At", String.valueOf(expiresAt)));
    }

    @Test
    void whenUsernameExists_thenReturnsConflict() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john123");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(userService.hasUserWithUsername("john123")).thenReturn(true);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenEmailExists_thenReturnsConflict() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john123");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(userService.hasUserWithUsername("john123")).thenReturn(false);
        when(userService.hasUserWithEmail("john@example.com")).thenReturn(true);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenValidSignIn_thenReturnsUserDtoWithCookiesAndHeader() throws Exception {
        SignInRequest request = new SignInRequest();
        request.setPassword("password123");
        request.setEmail("john@example.com");

        User user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setEmail("john@example.com");
        user.setRole("USER");
        user.setImageUrl("https://s3.example.com/avatar.png");

        UserDto userDto = new UserDto(
                1L,
                "john123",
                "John Doe",
                "john@example.com",
                "https://s3.example.com/avatar.png",
                "USER",
                Collections.emptyList(),
                Collections.emptyList()
        );

        String jwtToken = "jwt-token";
        String refreshTokenString = "refresh-token";
        long expiresAt = 123456789L;

        RefreshToken refreshToken = RefreshToken.builder()
                .id(10L)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusSeconds(3600))
                .user(user)
                .build();

        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(tokenProvider.generate(any(CustomUserDetails.class), eq(false))).thenReturn(jwtToken);
        when(tokenProvider.getTokenExpirationTimestamp(false)).thenReturn(expiresAt);
        when(refreshTokenService.createRefreshToken("john123")).thenReturn(refreshToken);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        CustomUserDetails principal = mock(CustomUserDetails.class);
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john123"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(cookie().value("accessToken", jwtToken))
                .andExpect(cookie().value("refreshToken", refreshTokenString))
                .andExpect(header().string("Access-Token-Expires-At", String.valueOf(expiresAt)));
    }

    @Test
    void whenEmailNotFound_thenReturnsUnauthorized() throws Exception {
        SignInRequest request = new SignInRequest();
        request.setPassword("password123");
        request.setEmail("unknown@example.com");

        when(userService.getUserByEmail("unknown@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidPassword_thenReturnsUnauthorized() throws Exception {
        SignInRequest request = new SignInRequest();
        request.setPassword("wrongpassword123");
        request.setEmail("john@example.com");

        User user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setEmail("john@example.com");
        user.setRole("USER");
        user.setImageUrl("https://s3.example.com/avatar.png");

        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenValidRefreshToken_thenReturns200WithNewTokens() throws Exception {
        String oldRefreshToken = "valid-refresh-token";
        String newRefreshToken = "new-refresh-token";
        String newAccessToken = "new-access-token";
        long expiresAt = 123456789L;

        User user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setEmail("john@example.com");
        user.setRole("USER");
        user.setImageUrl("https://s3.example.com/avatar.png");

        RefreshToken oldToken = RefreshToken.builder()
                .id(10L)
                .token(oldRefreshToken)
                .expiryDate(Instant.now().plusSeconds(3600))
                .user(user)
                .build();

        RefreshToken newToken = RefreshToken.builder()
                .id(11L)
                .token(newRefreshToken)
                .expiryDate(Instant.now().plusSeconds(7200))
                .user(user)
                .build();

        CustomUserDetails principal = mock(CustomUserDetails.class);
        when(principal.getUsername()).thenReturn("john123");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());


        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(refreshTokenService.findByToken(oldRefreshToken)).thenReturn(Optional.of(oldToken));
        doNothing().when(refreshTokenService).verifyExpiration(oldToken);
        when(refreshTokenService.createRefreshToken("john123")).thenReturn(newToken);
        when(tokenProvider.generate(principal, false)).thenReturn(newAccessToken);
        when(tokenProvider.getTokenExpirationTimestamp(false)).thenReturn(expiresAt);

        mockMvc.perform(post("/auth/refresh-token")
                        .cookie(new Cookie("refreshToken", oldRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("accessToken", newAccessToken))
                .andExpect(cookie().value("refreshToken", newRefreshToken))
                .andExpect(header().string("Access-Token-Expires-At", String.valueOf(expiresAt)));

        SecurityContextHolder.clearContext();
    }

    @Test
    void whenRefreshTokenMissing_then401() throws Exception {
        CustomUserDetails principal = mock(CustomUserDetails.class);

        mockMvc.perform(post("/auth/refresh-token")
                        .requestAttr("org.springframework.security.core.Authentication",
                                new UsernamePasswordAuthenticationToken(principal, null)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenRefreshTokenNotFound_then401() throws Exception {
        String token = "missing-token";
        CustomUserDetails principal = mock(CustomUserDetails.class);
        when(refreshTokenService.findByToken(token)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/refresh-token")
                        .cookie(new Cookie("refreshToken", token))
                        .requestAttr("org.springframework.security.core.Authentication",
                                new UsernamePasswordAuthenticationToken(principal, null)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void whenRefreshTokenExpired_then401() throws Exception {
        String token = "expired-token";

        User user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setEmail("john@example.com");
        user.setRole("USER");
        user.setImageUrl("https://s3.example.com/avatar.png");

        RefreshToken expiredToken = RefreshToken.builder()
                .id(11L)
                .token(token)
                .expiryDate(Instant.now().minusSeconds(10)) // expired
                .user(user)
                .build();

        CustomUserDetails principal = mock(CustomUserDetails.class);
        when(refreshTokenService.findByToken(token)).thenReturn(Optional.of(expiredToken));
        doThrow(new RuntimeException("Token expired"))
                .when(refreshTokenService).verifyExpiration(expiredToken);

        mockMvc.perform(post("/auth/refresh-token")
                        .cookie(new Cookie("refreshToken", token))
                        .requestAttr("org.springframework.security.core.Authentication",
                                new UsernamePasswordAuthenticationToken(principal, null)))
                .andExpect(status().isUnauthorized());
    }
}