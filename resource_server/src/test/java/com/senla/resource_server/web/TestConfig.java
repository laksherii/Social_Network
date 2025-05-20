package com.senla.resource_server.web;

import com.auth0.jwt.algorithms.Algorithm;
import com.senla.resource_server.config.JwtAuthenticationFilter;
import com.senla.resource_server.config.JwtTokenProvider;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.service.impl.CustomUserDetailsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public Algorithm testJwtAlgorithm() {
        return Algorithm.HMAC256("test-secret-key-1234567890-1234567890-1234567890");
    }

    @Bean
    public JwtTokenProvider testJwtTokenProvider(Algorithm algorithm) {
        return new JwtTokenProvider(algorithm);
    }

    @Bean
    @Primary
    public UserDao testUserDao() {
        return mock(UserDao.class);
    }

    @Bean
    public CustomUserDetailsService testCustomUserDetailsService(UserDao userDao) {
        return new CustomUserDetailsService(userDao);
    }

    @Bean
    public JwtAuthenticationFilter testJwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService customUserDetailsService
    ) {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public Validator testValidator() {
        return new LocalValidatorFactoryBean();
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