package com.senla.auth_server.config;

import com.senla.auth_server.service.dto.UserResponseDto;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class ResourceOwnerAuthenticationProvider implements AuthenticationProvider {

    private final RestClient restClient;

    public ResourceOwnerAuthenticationProvider(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://resource-server:8080").build();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            UserResponseDto userDto = restClient
                    .post()
                    .uri("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("email", username, "password", password))
                    .retrieve()
                    .body(UserResponseDto.class);

            if (userDto == null) {
                throw new BadCredentialsException("Invalid credentials");
            }

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userDto.getRole()));

            return new ResourceOwnerAuthenticationToken(
                    userDto.getId(),
                    username,
                    password,
                    authorities);

        } catch (RuntimeException ex) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ResourceOwnerAuthenticationToken.class.isAssignableFrom(authentication)
               || UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

