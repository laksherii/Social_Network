package com.senla.auth_server.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcClient jdbcClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return jdbcClient
                .sql("SELECT email, password FROM users WHERE email = ?")
                .params(email)
                .query((rs, rowNum) -> {
                    String username = rs.getString("email");
                    String password = rs.getString("password");

                    return User.builder()
                            .username(username)
                            .password(password)
                            .authorities(Collections.emptyList())
                            .build();
                })
                .optional()
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}

