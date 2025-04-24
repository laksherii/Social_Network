package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.data.mapper.UserMapper;
import com.senla.resource_server.exception.BadRequestParamException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserAuthRequestDto;
import com.senla.resource_server.service.dto.user.UserAuthResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl {

    private final UserDaoImpl userDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto findById(Long id) {
        User byId = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        return userMapper.toUserDto(byId);
    }

    public UserDto findByEmail(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
        return userMapper.toUserDto(user);
    }

    public CreateUserDtoResponse create(CreateUserDtoRequest userDtoRequest) {
        if (userDtoRequest.getEmail() == null || userDao.findByEmail(userDtoRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already exist");
        }
        User user = userMapper.toUserCreate(userDtoRequest);
        user.setRole(RoleType.ROLE_USER);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));
        userDao.save(user);
        return userMapper.toCreateUserDtoResponse(user);
    }

    public CreateUserDtoResponse createAdmin(CreateUserDtoRequest userDtoRequest) {
        if (userDtoRequest.getEmail() == null || userDao.findByEmail(userDtoRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already exist");
        }
        User user = userMapper.toUserCreate(userDtoRequest);
        user.setRole(RoleType.ROLE_ADMIN);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));
        userDao.save(user);
        return userMapper.toCreateUserDtoResponse(user);
    }

    public Mono<UserAuthResponseDto> authenticate(UserAuthRequestDto userDtoRequest) {
        User user = userDao.findByEmail(userDtoRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (passwordEncoder.matches(userDtoRequest.getPassword(), user.getPassword())) {
            UserAuthResponseDto userAuthResponseDto = userMapper.toUserAuthResponseDto(user);
            return Mono.just(userAuthResponseDto);
        }
        return Mono.empty();
    }

    public UpdateUserDtoResponse update(UpdateUserDtoRequest userDtoRequest) {
        if (userDtoRequest == null ){
            throw new BadRequestParamException("UserDto request is null");
        }
        User user = userDao.findByEmail(userDtoRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found by id=" + userDtoRequest.getEmail()));
        user.setFirstName(userDtoRequest.getFirstName());
        user.setLastName(userDtoRequest.getLastName());
        user.setBirthDay(userDtoRequest.getBirthDay());
        user.setGender(userDtoRequest.getGender());
        User update = userDao.update(user);
        return userMapper.updateUserResponseToDto(update);
    }
}
