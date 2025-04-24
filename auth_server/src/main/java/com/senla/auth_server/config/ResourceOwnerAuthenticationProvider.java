package com.senla.auth_server.config;

import com.senla.auth_server.exception.BadCredentialsException;
import com.senla.auth_server.service.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
@Component
public class ResourceOwnerAuthenticationProvider implements AuthenticationProvider {

    private final WebClient webClient;

    public ResourceOwnerAuthenticationProvider(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build(); // ресурс-сервер
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();


        try {
            UserResponseDto userDto = webClient
                    .post()
                    .uri("http://localhost:8080/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("email", username, "password", password))
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .block();

            if (userDto == null) {
                throw new BadCredentialsException("Invalid credentials");
            }

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userDto.getRole()));

            return new ResourceOwnerAuthenticationToken(username, password, authorities);

        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ResourceOwnerAuthenticationToken.class.isAssignableFrom(authentication)
               || UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

