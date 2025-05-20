package com.senla.auth_server.web;

import com.senla.auth_server.service.dto.UserDtoRequest;
import com.senla.auth_server.service.dto.UserDtoResponse;
import com.senla.auth_server.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class RestUserController {

    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDtoResponse createUser(@Valid @RequestBody UserDtoRequest createUserDtoRequest) {
        UserDtoResponse response = userService.create(createUserDtoRequest);
        log.info("Successfully created new user with email: {}", createUserDtoRequest.getEmail());
        return response;
    }
}
