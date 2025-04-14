package com.senla.social_network.web;

import com.senla.social_network.service.dto.user.CreateUserDtoRequest;
import com.senla.social_network.service.dto.user.CreateUserDtoResponse;
import com.senla.social_network.service.dto.user.UserDto;
import com.senla.social_network.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class RestUserController {
    private final UserServiceImpl userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public CreateUserDtoResponse createUser(@RequestBody CreateUserDtoRequest createUserDtoRequest) {
        return userService.create(createUserDtoRequest);
    }
}
