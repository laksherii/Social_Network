package com.senla.auth_server.web;

import com.senla.auth_server.service.dto.AuthDtoRequest;
import com.senla.auth_server.service.dto.AuthDtoResponse;
import com.senla.auth_server.service.dto.RefreshDtoToken;
import com.senla.auth_server.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class RestAuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthDtoResponse login(@Valid @RequestBody AuthDtoRequest request) {
        AuthDtoResponse response = authService.login(request);
        log.info("Login successful for email: {}", request.getEmail());
        return response;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public AuthDtoResponse refresh(@Valid @RequestBody RefreshDtoToken refreshDtoToken) {
        AuthDtoResponse response = authService.refresh(refreshDtoToken);
        log.info("Token refreshed successfully");
        return response;
    }
}

