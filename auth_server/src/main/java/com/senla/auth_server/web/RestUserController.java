package com.senla.auth_server.web;

import com.senla.auth_server.service.dto.UserDto;
import com.senla.auth_server.service.dto.UserRegistrationRequest;
import com.senla.auth_server.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class RestUserController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findByEmail(@PathVariable String email) {
        return userServiceImpl.findByEmail(email);
    }
}
