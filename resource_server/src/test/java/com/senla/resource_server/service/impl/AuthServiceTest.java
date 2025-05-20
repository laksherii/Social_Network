package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setupSecurityContext() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUser_shouldReturnUser_whenUserExists() {
        String email = "test@example.com";
        User expectedUser = User.builder().email(email).build();

        when(authentication.getName()).thenReturn(email);
        when(userDao.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        User actualUser = authService.getCurrentUser();

        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void getCurrentUser_shouldThrowEntityNotFoundException_whenUserNotFound() {
        String email = "notfound@example.com";

        when(authentication.getName()).thenReturn(email);
        when(userDao.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser())
                .isInstanceOf(EntityNotFoundException.class);

        verify(userDao).findByEmail(email);
    }
}