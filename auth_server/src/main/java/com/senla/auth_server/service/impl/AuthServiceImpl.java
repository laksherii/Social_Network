package com.senla.auth_server.service.impl;

import com.senla.auth_server.service.dto.UserRequestDto;
import com.senla.auth_server.service.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final WebClient.Builder webClientBuilder;

    public Mono<UserResponseDto> authenticate(UserRequestDto loginRequest) {
        String url = "http://localhost:8080/authenticate";

            return webClientBuilder.build()
                    .post()
                    .uri(url)
                    .bodyValue(loginRequest)
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .onErrorResume(e -> Mono.empty());
        }
}

