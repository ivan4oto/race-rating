package com.ivangochev.raceratingapi.user.passwordreset;

import com.ivangochev.raceratingapi.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tokenHash;

    private LocalDateTime expiry;

    private boolean used;

    @ManyToOne(optional = false)
    private User user;
}
