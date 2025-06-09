package com.senla.resource_server.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Algorithm algorithm;

    public boolean validateAccessToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return JWT.require(algorithm).build().verify(token).getSubject();
    }

}

