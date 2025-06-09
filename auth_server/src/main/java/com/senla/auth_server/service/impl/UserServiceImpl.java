package com.senla.auth_server.service.impl;

import com.senla.auth_server.data.UserDao;
import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.exception.EmailAlreadyExistsException;
import com.senla.auth_server.service.UserService;
import com.senla.auth_server.service.dto.UserDtoRequest;
import com.senla.auth_server.service.dto.UserDtoResponse;
import com.senla.auth_server.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDtoResponse create(UserDtoRequest userDtoRequest) {
        log.info("Attempting to create user with email: {}", userDtoRequest.getEmail());

        if (userDao.findByEmail(userDtoRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already exist");
        }

        User user = userMapper.toUserFromUserDtoRequest(userDtoRequest);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));

        User saved = userDao.save(user);

        log.info("User created successfully with email: {}", saved.getEmail());
        return userMapper.toUserDtoResponse(saved);
    }
}
