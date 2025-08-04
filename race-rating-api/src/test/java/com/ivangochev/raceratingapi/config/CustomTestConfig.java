package com.ivangochev.raceratingapi.config;

import com.ivangochev.raceratingapi.security.CustomUserDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

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

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup").permitAll()
                        .requestMatchers("/auth/signin").permitAll()
                        .requestMatchers("/auth/refresh-token", "/auth/logout").authenticated()
                        .anyRequest().denyAll() // block everything else
                )
                .sessionManagement().disable()
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .httpBasic().disable()
                .formLogin().disable();

        return http.build();
    }
}
