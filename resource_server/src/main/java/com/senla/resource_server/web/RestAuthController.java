package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.user.UserAuthRequestDto;
import com.senla.resource_server.service.dto.user.UserAuthResponseDto;
import com.senla.resource_server.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/authenticate")
public class RestAuthController {

    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UserAuthResponseDto>> authenticate(@Valid @RequestBody UserAuthRequestDto userAuthRequestDto) {
        log.info("Authentication attempt for email: {}", userAuthRequestDto.getEmail());

        return userService.authenticate(userAuthRequestDto)
                .map(userAuthResponseDto -> {
                    log.info("Authentication successful for email: {}", userAuthRequestDto.getEmail());
                    return ResponseEntity.ok(userAuthResponseDto);
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
