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
    @JoinTable(
            name = "users_voted_for_races",                               // Actual table name
            joinColumns = @JoinColumn(name = "user_id"),                  // FK back to User.id
            inverseJoinColumns = @JoinColumn(name = "voted_for_races_id") // FK to Race.id
    )
    private List<Race> votedForRaces = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "users_commented_for_races",                               // Actual table name
            joinColumns = @JoinColumn(name = "user_id"),                      // FK back to User.id
            inverseJoinColumns = @JoinColumn(name = "commented_for_races_id") // FK to Race.id
    )
    private List<Race> commentedForRaces = new ArrayList<>();

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

    public boolean isAdmin() {
        return this.role.equals("ADMIN");
    }
}
