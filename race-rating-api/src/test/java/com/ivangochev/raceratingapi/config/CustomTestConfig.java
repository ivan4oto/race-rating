package com.ivangochev.raceratingapi.config;

import com.ivangochev.raceratingapi.security.CustomUserDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@TestConfiguration
public class CustomTestConfig {
    @Bean(name = "userDetailsServiceMock")
    @Primary
    public UserDetailsService userDetailsService() {
        return username -> {
            if ("ivan".equals(username)) {
                CustomUserDetails customUserDetails = new CustomUserDetails();
                customUserDetails.setId(1L);
                customUserDetails.setUsername("ivan");
                customUserDetails.setEmail("ivan@abv.bg");
                return customUserDetails;
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        };
    }
}
