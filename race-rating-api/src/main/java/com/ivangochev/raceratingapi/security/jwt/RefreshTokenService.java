package com.ivangochev.raceratingapi.security.jwt;

import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshToken createRefreshToken(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s not found", username)));
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(user);
        existingRefreshToken.ifPresent(refreshTokenRepository::delete);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(60*1000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
    public void verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please login!");
        }
    }


}
