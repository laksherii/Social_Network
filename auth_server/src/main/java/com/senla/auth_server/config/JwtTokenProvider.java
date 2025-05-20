package com.senla.auth_server.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.senla.auth_server.exception.NoValidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(Algorithm algorithm,
                            @Value("${jwt.validateTimeForAccessToken}") long accessTokenValidity,
                            @Value("${jwt.validateTimeForRefreshToken}") long refreshTokenValidity) {
        this.algorithm = algorithm;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String generateAccessToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(accessTokenValidity))
                .sign(algorithm);
    }

    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(refreshTokenValidity))
                .sign(algorithm);
    }

    public DecodedJWT decodeRefreshToken(String token) {
        try {
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new NoValidTokenException("Invalid refresh token");
        }
    }
}

