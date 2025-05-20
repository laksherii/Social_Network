package com.senla.auth_server.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcUserDetailsService implements UserDetailsService {

    private final JdbcClient jdbcClient;

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("Attempting to load user by email: {}", email);

        return jdbcClient
                .sql("SELECT email, password FROM users WHERE email = ?")
                .params(email)
                .query((rs, rowNum) -> {
                    String username = rs.getString("email");
                    String password = rs.getString("password");

                    log.info("User found in database: {}", username);

                    return User.builder()
                            .username(username)
                            .password(password)
                            .authorities(Collections.emptyList())
                            .build();
                })
                .optional()
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));
    }
}
