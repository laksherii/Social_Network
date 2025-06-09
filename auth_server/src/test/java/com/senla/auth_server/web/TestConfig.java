package com.senla.auth_server.web;

import com.auth0.jwt.algorithms.Algorithm;
import com.senla.auth_server.config.JwtTokenProvider;
import com.senla.auth_server.service.impl.JdbcUserDetailsService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public Algorithm testJwtAlgorithm() {
        return Algorithm.HMAC256("test-secret-key-1234567890-1234567890-1234567890");
    }

    @Bean
    public JwtTokenProvider testJwtTokenProvider(
            Algorithm algorithm,
            @Value("${jwt.validateTimeForAccessToken}") long accessTokenValidity,
            @Value("${jwt.validateTimeForRefreshToken}") long refreshTokenValidity) {
        return new JwtTokenProvider(algorithm, accessTokenValidity, refreshTokenValidity);
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5436/auth_server_db");
        dataSource.setUsername("postgres");
        dataSource.setPassword("root");
        return dataSource;
    }

    @Bean
    public JdbcClient jdbcClient(DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    public JdbcUserDetailsService userDetailsService(JdbcClient jdbcClient) {
        return new JdbcUserDetailsService(jdbcClient);
    }


    @Bean
    public MethodValidationPostProcessor testMethodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}