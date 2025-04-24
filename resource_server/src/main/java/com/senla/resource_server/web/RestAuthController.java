package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.user.UserAuthRequestDto;
import com.senla.resource_server.service.dto.user.UserAuthResponseDto;
import com.senla.resource_server.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authenticate")
public class RestAuthController {

    private final UserServiceImpl userService;

    @PostMapping
    public Mono<ResponseEntity<UserAuthResponseDto>> authenticate(@RequestBody UserAuthRequestDto userAuthRequestDto) {
        return userService.authenticate(userAuthRequestDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
