package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.service.interfaces.UserService;
import com.senla.resource_server.service.mapper.UserMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserAuthRequestDto;
import com.senla.resource_server.service.dto.user.UserAuthResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDto findById(Long id) {
        log.info("Fetching user by ID: {}", id);

        User byId = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        log.info("User found: {} (ID: {})", byId.getEmail(), byId.getId());

        return userMapper.toUserDto(byId);
    }

    @Override
    public UserDto findByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));

        log.info("User found: {} (ID: {})", user.getEmail(), user.getId());

        return userMapper.toUserDto(user);
    }

    @Override
    public UserInfoDto getUserInfo(String email) {
        log.info("Fetching user info by email: {}", email);

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));

        log.info("User info fetched: {} (ID: {})", user.getEmail(), user.getId());

        return userMapper.toUserInfoDto(user);
    }

    @Override
    public CreateUserDtoResponse create(CreateUserDtoRequest userDtoRequest) {
        log.info("Creating user with email: {}", userDtoRequest.getEmail());

        if (userDao.findByEmail(userDtoRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already exist");
        }

        User user = userMapper.toUserCreate(userDtoRequest);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));

        Wall wall = new Wall();
        wall.setOwner(user);
        user.setWall(wall);

        User saved = userDao.save(user);

        log.info("User successfully created: {} (ID: {})", saved.getEmail(), saved.getId());

        return userMapper.toCreateUserDtoResponse(saved);
    }

    @Override
    public Mono<UserAuthResponseDto> authenticate(UserAuthRequestDto userDtoRequest) {
        log.info("Authenticating user with email: {}", userDtoRequest.getEmail());

        User user = userDao.findByEmail(userDtoRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("User found: {} (ID: {})", user.getEmail(), user.getId());

        if (passwordEncoder.matches(userDtoRequest.getPassword(), user.getPassword())) {
            log.info("User authenticated successfully: {} (ID: {})", user.getEmail(), user.getId());
            UserAuthResponseDto userAuthResponseDto = userMapper.toUserAuthResponseDto(user);
            return Mono.just(userAuthResponseDto);
        }
        log.info("Authentication failed for user: {}", userDtoRequest.getEmail());
        return Mono.empty();
    }

    @Override
    public UpdateUserDtoResponse update(UpdateUserDtoRequest userDtoRequest) {
        log.info("Updating user with email: {}", userDtoRequest.getEmail());

        User user = userDao.findByEmail(userDtoRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found by email=" + userDtoRequest.getEmail()));
        log.info("User found: {} (ID: {})", user.getEmail(), user.getId());

        if (userDtoRequest.getFirstName() != null) {
            user.setFirstName(userDtoRequest.getFirstName());
        }

        if (userDtoRequest.getLastName() != null) {
            user.setLastName(userDtoRequest.getLastName());
        }

        if (userDtoRequest.getGender() != null) {
            user.setGender(userDtoRequest.getGender());
        }

        if (userDtoRequest.getBirthDay() != null) {
            user.setBirthDay(userDtoRequest.getBirthDay());
        }

        User updatedUser = userDao.update(user);

        log.info("User updated successfully: {} (ID: {})", updatedUser.getEmail(), updatedUser.getId());

        return userMapper.updateUserResponseToDto(updatedUser);
    }

    @Override
    public List<UserDto> searchUsers(UserSearchDto userSearchDto) {
        log.info("Searching users by criteria: {}", userSearchDto);

        List<User> users = userDao.searchUser(userSearchDto);

        log.info("Found {} users matching search criteria", users.size());

        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }
}