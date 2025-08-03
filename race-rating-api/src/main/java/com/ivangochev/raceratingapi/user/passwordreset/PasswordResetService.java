package com.ivangochev.raceratingapi.user.passwordreset;

import com.ivangochev.raceratingapi.aws.EmailService;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${app.public-domain}")
    private String uiPublicDomain;

    public PasswordResetService(UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordResetTokenRepository tokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }


    public void sendPasswordResetEmail(String email) {
        log.info("Send password reset email {}", email);
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.info("No user found for email: {}", email); // Do not expose this to frontend
            return;
        }

        var user = userOpt.get();
        var token = generateSecureToken();
        var tokenHash = passwordEncoder.encode(token);
        var expiry = LocalDateTime.now().plusMinutes(30);

        var resetToken = new PasswordResetToken();
        resetToken.setTokenHash(tokenHash);
        resetToken.setUser(user);
        resetToken.setExpiry(expiry);
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        String resetLink = "https://" + uiPublicDomain + "/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String body = String.format(
                """
                        Hello,
                        
                        You requested a password reset. Click the link below to reset your password:
                        %s
                        
                        If you did not request this, please ignore this email.
                        
                        Regards,
                        Race Rating Team""",
                resetLink
        );
        emailService.sendEmail(email, subject, body);
    }

    public void processPasswordReset(String rawToken, String newPassword) {
        // Find matching token record by comparing hash
        var tokens = tokenRepository.findAll();
        var tokenOpt = tokens.stream()
                .filter(token -> passwordEncoder.matches(rawToken, token.getTokenHash()))
                .findFirst();

        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        var token = tokenOpt.get();

        if (token.isUsed() || token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        log.info("Password reset successful for user {}", user.getEmail());
    }



    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
