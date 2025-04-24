package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class RestUserController {
    private final UserServiceImpl userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or ('MODERATOR')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostAuthorize("hasRole('ADMIN') or #email == authentication.name ")
    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserDtoResponse createUser(@RequestBody CreateUserDtoRequest createUserDtoRequest) {
        return userService.create(createUserDtoRequest);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserDtoResponse createAdmin(@RequestBody CreateUserDtoRequest createUserDtoRequest) {
        return userService.createAdmin(createUserDtoRequest);
    }

    @PreAuthorize("#updateUserDtoRequest.email == authentication.name or hasRole('ADMIN')")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UpdateUserDtoResponse updateUser(@RequestBody UpdateUserDtoRequest updateUserDtoRequest) {
        return userService.update(updateUserDtoRequest);
    }
}
