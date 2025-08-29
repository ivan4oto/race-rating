package com.ivangochev.raceratingapi.user;

import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u.id from User u")
    List<Long> findAllIds();

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByProvider(OAuth2Provider provider);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
