package com.senla.resource_server.web;

import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import com.senla.resource_server.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class RestUserController {

    private final UserServiceImpl userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        UserDto user = userService.findById(id);
        log.info("Successfully fetched user with id: {}", id);
        return user;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByEmail(@PathVariable String email) {
        log.info("Fetching user with email: {}", email);
        UserDto user = userService.findByEmail(email);
        log.info("Successfully fetched user with email: {}", email);
        return user;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/info/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoDto getUserInfo(@PathVariable String email) {
        log.info("Fetching user info for email: {}", email);
        UserInfoDto userInfo = userService.getUserInfo(email);
        log.info("Successfully fetched user info for email: {}", email);
        return userInfo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserDtoResponse createUser(@Valid @RequestBody CreateUserDtoRequest createUserDtoRequest) {
        log.info("Creating new user with email: {}", createUserDtoRequest.getEmail());
        CreateUserDtoResponse response = userService.create(createUserDtoRequest);
        log.info("Successfully created new user with email: {}", createUserDtoRequest.getEmail());
        return response;
    }

    @PreAuthorize("#updateUserDtoRequest.email == authentication.name or hasRole('ADMIN')")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UpdateUserDtoResponse updateUser(@Valid @RequestBody UpdateUserDtoRequest updateUserDtoRequest) {
        log.info("Updating user with email: {}", updateUserDtoRequest.getEmail());
        UpdateUserDtoResponse response = userService.update(updateUserDtoRequest);
        log.info("Successfully updated user with email: {}", updateUserDtoRequest.getEmail());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) GenderType gender) {

        log.info("Searching users with userSearchDto: firstName={}, lastName={}, age={}, gender={}",
                firstName, lastName, age, gender);

        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setFirstName(firstName);
        userSearchDto.setLastName(lastName);
        userSearchDto.setAge(age);
        userSearchDto.setGender(gender);

        List<UserDto> users = userService.searchUsers(userSearchDto);
        log.info("Successfully found {} users matching the search userSearchDto.", users.size());
        return users;
    }
}
