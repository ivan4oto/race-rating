package com.ivangochev.raceratingapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Component
public class TokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration.minutes}")
    private Long jwtExpirationMinutes;

    public String generate(CustomUserDetails customUserDetails, Boolean rememberMe) {
        List<String> roles = customUserDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        byte[] signingKey = jwtSecret.getBytes();

        return Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setExpiration(getTokenExpirationTime(rememberMe))
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setId(UUID.randomUUID().toString())
                .setIssuer(TOKEN_ISSUER)
                .setAudience(TOKEN_AUDIENCE)
                .setSubject(customUserDetails.getUsername())
                .claim("rol", roles)
                .claim("name", customUserDetails.getName())
                .claim("preferred_username", customUserDetails.getUsername())
                .claim("email", customUserDetails.getEmail())
                .claim("avatarUrl", customUserDetails.getAvatarUrl())
                .compact();
    }

    public Optional<Jws<Claims>> getJwtsClaims(String token) {
        try {
            byte[] signingKey = jwtSecret.getBytes();

            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);

            return Optional.of(jws);
        } catch (ExpiredJwtException exception) {
            log.error("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
        } catch (SignatureException exception) {
            log.error("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
        }
        return Optional.empty();
    }

    private Date getTokenExpirationTime(Boolean rememberMe) {
        if (rememberMe) {
            long jwtLongExpirationSeconds = 30 * 24 * 60 * 60 * 1000;
            return new Date(System.currentTimeMillis() + jwtLongExpirationSeconds);
        };
        return Date.from(ZonedDateTime.now().plusMinutes(jwtExpirationMinutes).toInstant());
    }

    public long getTokenExpirationTimestamp(Boolean rememberMe) {
        return getTokenExpirationTime(rememberMe).getTime() / 1000;
    }

    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "race-rating-api";
    public static final String TOKEN_AUDIENCE = "race-rating-ui";
}
