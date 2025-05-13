package com.senla.auth_server.web;

import com.senla.auth_server.service.dto.AuthDtoRequest;
import com.senla.auth_server.service.dto.AuthDtoResponse;
import com.senla.auth_server.service.dto.RefreshDtoToken;
import com.senla.auth_server.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RestAuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthDtoResponse login(@RequestBody AuthDtoRequest request) {
        return authService.login(request);
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public AuthDtoResponse refresh(@RequestBody RefreshDtoToken refreshDtoToken) {
        return authService.refresh(refreshDtoToken);
    }
}
