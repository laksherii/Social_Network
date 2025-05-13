package com.senla.auth_server.service.impl;

import com.senla.auth_server.config.JwtTokenProvider;
import com.senla.auth_server.data.UserDao;
import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.exception.EmailAlreadyExistsException;
import com.senla.auth_server.exception.EntityNotFoundException;
import com.senla.auth_server.service.UserService;
import com.senla.auth_server.service.dto.UpdateUserDtoRequest;
import com.senla.auth_server.service.dto.UpdateUserDtoResponse;
import com.senla.auth_server.service.dto.UserDto;
import com.senla.auth_server.service.dto.UserDtoRequest;
import com.senla.auth_server.service.dto.UserDtoResponse;
import com.senla.auth_server.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RestClient restClientBuilder;

    @Value("${jwt.resourceServerUrl}")
    private String resourceServerUrl;

    public UserDtoResponse create(UserDtoRequest userDtoRequest) {
        log.info("Creating user with email: {}", userDtoRequest.getEmail());

        if (userDao.findByEmail(userDtoRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already exist");
        }


        User user = userMapper.toUserFromUserDtoRequest(userDtoRequest);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));

        User saved = userDao.save(user);
        log.info("User successfully created: {} (ID: {})", saved.getEmail(), saved.getId());

        UserDtoResponse userDtoResponse = userMapper.toUserDtoResponse(saved);

        try {
            restClientBuilder.post()
                    .uri(resourceServerUrl + "/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userDtoResponse)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("Remote user creation failed " + e.getMessage());
        }

        return userDtoResponse;
    }
}
