package com.senla.auth_server.web;

import com.senla.auth_server.service.UserService;
import com.senla.auth_server.service.dto.UserDto;
import com.senla.auth_server.service.dto.UserRegistrationRequest;
import com.senla.auth_server.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RestAuthController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest request) {
        UserDto register = userServiceImpl.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(register.toString());
    }
}