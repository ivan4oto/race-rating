package com.ivangochev.raceratingapi.runner.e2e;

import com.ivangochev.raceratingapi.security.WebSecurityConfig;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("e2e")
@Slf4j
@Component
public class AdminUserInit implements CommandLineRunner {
    private final Environment env;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AdminUserInit(Environment env, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.env = env;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String username = env.getProperty("E2E_ADMIN_USERNAME", "e2e_admin");
        String rawPassword = env.getProperty("E2E_ADMIN_PASSWORD", "e2e_password");
        String email = env.getProperty("E2E_ADMIN_EMAIL", "e2e-admin@example.com");

        log.info("Initializing admin user for e2e tests");
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setProvider(OAuth2Provider.LOCAL);
        user.setRole(WebSecurityConfig.ADMIN);

        userRepository.save(user);
        log.info("Admin user for e2e tests has been created");
    }
}
