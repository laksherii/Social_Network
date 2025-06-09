package com.senla.auth_server.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.ResultSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class JdbcUserDetailsServiceTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<UserDetails> mappedQuerySpec;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private JdbcUserDetailsService userDetailsService;


    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() throws Exception {
        // given
        String email = "test@example.com";
        String password = "password123";

        when(resultSet.getString("email")).thenReturn(email);
        when(resultSet.getString("password")).thenReturn(password);

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.params(email)).thenReturn(statementSpec);
        when(statementSpec.query(any(RowMapper.class))).thenAnswer(invocation -> {
            RowMapper<UserDetails> mapper = invocation.getArgument(0);
            UserDetails mappedUser = mapper.mapRow(resultSet, 1);
            when(mappedQuerySpec.optional()).thenReturn(Optional.of(mappedUser));
            return mappedQuerySpec;
        });

        // when
        UserDetails actualUser = userDetailsService.loadUserByUsername(email);

        // then
        assertThat(actualUser.getUsername()).isEqualTo(email);
        assertThat(actualUser.getPassword()).isEqualTo(password);
        assertThat(actualUser.getAuthorities()).isEmpty();
    }


    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        // given
        String email = "notfound@example.com";

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.params(email)).thenReturn(statementSpec);
        when(statementSpec.query(any(RowMapper.class))).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.optional()).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + email);
    }
}