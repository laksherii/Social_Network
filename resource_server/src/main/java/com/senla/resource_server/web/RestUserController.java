package com.senla.resource_server.web;

import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import com.senla.resource_server.service.dto.user.UserSearchDtoResponse;
import com.senla.resource_server.service.interfaces.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable @Positive Long id) {
        UserDto user = userService.findById(id);
        log.info("Successfully fetched user with id: {}", id);
        return user;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByEmail(@PathVariable @Email String email) {
        UserDto user = userService.findByEmail(email);
        log.info("Successfully fetched user with email: {}", email);
        return user;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/info/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoDto getUserInfo(@PathVariable @Email String email) {
        UserInfoDto userInfo = userService.getUserInfo(email);
        log.info("Successfully fetched user info for email: {}", email);
        return userInfo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserDtoResponse createUser(@Valid @RequestBody CreateUserDtoRequest createUserDtoRequest) {
        CreateUserDtoResponse response = userService.create(createUserDtoRequest);
        log.info("Successfully created new user with email: {}", createUserDtoRequest.getEmail());
        return response;
    }

    @PreAuthorize("#updateUserDtoRequest.email == authentication.name or hasRole('ADMIN')")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserSearchDtoResponse updateUser(@Valid @RequestBody UpdateUserDtoRequest updateUserDtoRequest) {
        UserSearchDtoResponse response = userService.update(updateUserDtoRequest);
        log.info("Successfully updated user: {}", updateUserDtoRequest);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public UpdateRoleUserDtoResponse updateRoleUser(@Valid @RequestBody UpdateRoleUserDtoRequest updateRoleUserDtoRequest) {
        UpdateRoleUserDtoResponse updateRoleUserDtoResponse = userService.updateRole(updateRoleUserDtoRequest);
        log.info("Successfully updated role user with email: {}", updateRoleUserDtoRequest.getEmail());
        return updateRoleUserDtoResponse;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<UserSearchDtoResponse> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) GenderType gender) {

        UserSearchDto userSearchDto = UserSearchDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .age(age)
                .gender(gender)
                .build();

        List<UserSearchDtoResponse> users = userService.searchUsers(userSearchDto);

        log.info("Search users request - firstName: {}, lastName: {}, age: {}, gender: {}. Found: {} result(s)",
                firstName, lastName, age, gender, users.size());

        return users;
    }
}
