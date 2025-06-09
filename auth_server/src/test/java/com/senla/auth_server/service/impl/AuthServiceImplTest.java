package com.senla.auth_server.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.senla.auth_server.config.JwtTokenProvider;
import com.senla.auth_server.service.dto.AuthDtoRequest;
import com.senla.auth_server.service.dto.AuthDtoResponse;
import com.senla.auth_server.service.dto.RefreshDtoToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    void login_shouldReturnAuthDtoResponse_whenCredentialsAreValid() {
        // given
        AuthDtoRequest request = AuthDtoRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        Authentication authenticationMock = mock(Authentication.class);
        User userDetails = new User(request.getEmail(), request.getPassword(), java.util.Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(request.getEmail())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(request.getEmail())).thenReturn("refresh-token");

        // when
        AuthDtoResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateAccessToken(request.getEmail());
        verify(jwtTokenProvider).generateRefreshToken(request.getEmail());
    }

    @Test
    void refresh_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        // given
        RefreshDtoToken refreshDtoToken = RefreshDtoToken.builder()
                .refreshToken("valid-refresh-token")
                .build();

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(jwtTokenProvider.decodeRefreshToken("valid-refresh-token")).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("test@example.com");
        when(jwtTokenProvider.generateAccessToken("test@example.com")).thenReturn("new-access-token");

        // when
        AuthDtoResponse response = authService.refresh(refreshDtoToken);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("valid-refresh-token");

        verify(jwtTokenProvider).decodeRefreshToken("valid-refresh-token");
        verify(jwtTokenProvider).generateAccessToken("test@example.com");
    }
}
