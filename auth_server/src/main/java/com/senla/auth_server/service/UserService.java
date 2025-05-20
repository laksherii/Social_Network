package com.senla.auth_server.service;

import com.senla.auth_server.service.dto.UserDtoRequest;
import com.senla.auth_server.service.dto.UserDtoResponse;

public interface UserService {
    UserDtoResponse create(UserDtoRequest userDtoRequest);
}
