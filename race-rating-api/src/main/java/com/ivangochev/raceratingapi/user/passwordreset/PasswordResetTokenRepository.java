package com.ivangochev.raceratingapi.user.passwordreset;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
}
