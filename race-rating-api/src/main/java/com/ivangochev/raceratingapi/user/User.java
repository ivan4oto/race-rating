package com.ivangochev.raceratingapi.user;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String name;
    private String email;
    private String role;
    private String imageUrl;

    @ManyToMany
    private List<Race> votedForRaces = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    private String providerId;

    public User(String username, String password, String name, String email, String role, String imageUrl, OAuth2Provider provider, String providerId) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.imageUrl = imageUrl;
        this.provider = provider;
        this.providerId = providerId;
    }
}
