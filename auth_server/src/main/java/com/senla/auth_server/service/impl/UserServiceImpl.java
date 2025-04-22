package com.senla.auth_server.service.impl;

import com.senla.auth_server.data.dao.UserDao;
import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.service.UserService;
import com.senla.auth_server.service.dto.UserDto;
import com.senla.auth_server.service.dto.UserDtoToResourceServer;
import com.senla.auth_server.service.dto.UserRegistrationRequest;
import com.senla.auth_server.service.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserRegistrationRequest request) {
        if (userDao.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userDao.save(user);
        UserDto userDto = userMapper.toUserDto(user);

        UserDtoToResourceServer userDtoToResourceServer = userMapper.toUserDtoToResourceServer(user);

        restTemplate.postForEntity("http://localhost:8080/user", userDtoToResourceServer, String.class);
        return userDto;
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toUserDto(user);
    }
}
