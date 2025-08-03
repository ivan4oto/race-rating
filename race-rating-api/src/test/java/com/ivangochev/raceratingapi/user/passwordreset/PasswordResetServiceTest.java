package com.ivangochev.raceratingapi.user.passwordreset;

import com.ivangochev.raceratingapi.aws.EmailService;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "uiPublicDomain", "example.com");
    }

    @Test
    void sendPasswordResetEmail_userExists_sendsEmailAndStoresToken() {
        var user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);

        passwordResetService.sendPasswordResetEmail("test@example.com");

        verify(tokenRepository).save(tokenCaptor.capture());
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), contains("https://example.com/reset-password"));

        var savedToken = tokenCaptor.getValue();
        assertEquals(user, savedToken.getUser());
        assertEquals("hashedToken", savedToken.getTokenHash());
        assertFalse(savedToken.isUsed());
        assertTrue(savedToken.getExpiry().isAfter(LocalDateTime.now()));
    }

    @Test
    void sendPasswordResetEmail_userNotFound_doesNothing() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        passwordResetService.sendPasswordResetEmail("notfound@example.com");

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void processPasswordReset_validToken_updatesPasswordAndMarksTokenUsed() {
        var user = new User();
        user.setEmail("reset@example.com");

        var token = new PasswordResetToken();
        token.setTokenHash("hashedToken");
        token.setUser(user);
        token.setUsed(false);
        token.setExpiry(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findAll()).thenReturn(List.of(token));
        when(passwordEncoder.matches("rawToken", "hashedToken")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        passwordResetService.processPasswordReset("rawToken", "newPass");

        assertEquals("encodedPass", user.getPassword());
        assertTrue(token.isUsed());
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void processPasswordReset_invalidToken_throwsException() {
        when(tokenRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                passwordResetService.processPasswordReset("badToken", "password"));
    }

    @Test
    void processPasswordReset_expiredToken_throwsException() {
        var token = new PasswordResetToken();
        token.setTokenHash("hashedToken");
        token.setUser(new User());
        token.setUsed(false);
        token.setExpiry(LocalDateTime.now().minusMinutes(5));

        when(tokenRepository.findAll()).thenReturn(List.of(token));
        when(passwordEncoder.matches("rawToken", "hashedToken")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                passwordResetService.processPasswordReset("rawToken", "newPass"));
    }

    @Test
    void processPasswordReset_alreadyUsedToken_throwsException() {
        var token = new PasswordResetToken();
        token.setTokenHash("hashedToken");
        token.setUser(new User());
        token.setUsed(true);
        token.setExpiry(LocalDateTime.now().plusMinutes(5));

        when(tokenRepository.findAll()).thenReturn(List.of(token));
        when(passwordEncoder.matches("rawToken", "hashedToken")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                passwordResetService.processPasswordReset("rawToken", "newPass"));
    }
}
