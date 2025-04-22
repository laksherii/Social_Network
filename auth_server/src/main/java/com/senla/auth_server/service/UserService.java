package com.senla.auth_server.service;

import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.service.dto.UserDto;
import com.senla.auth_server.service.dto.UserRegistrationRequest;

public interface UserService {
    UserDto register(UserRegistrationRequest request);
    UserDto findByEmail(String email);
}
