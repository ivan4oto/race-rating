package com.ivangochev.raceratingapi.security.jwt;

import com.ivangochev.raceratingapi.user.User;
import org.springframework.data.repository.CrudRepository;

import java.sql.Ref;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
