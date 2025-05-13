package com.senla.auth_server.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.senla.auth_server.config.JwtTokenProvider;
import com.senla.auth_server.service.dto.AuthDtoRequest;
import com.senla.auth_server.service.dto.AuthDtoResponse;
import com.senla.auth_server.service.dto.RefreshDtoToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthDtoResponse login(AuthDtoRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());

        return new AuthDtoResponse(accessToken, refreshToken);
    }

    public AuthDtoResponse refresh(RefreshDtoToken refreshDtoToken) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeRefreshToken(refreshDtoToken.getRefreshToken());
        String username = decodedJWT.getSubject();

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        return new AuthDtoResponse(newAccessToken, refreshDtoToken.getRefreshToken());
    }
}
