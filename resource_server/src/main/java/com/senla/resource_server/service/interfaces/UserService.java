package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserAuthRequestDto;
import com.senla.resource_server.service.dto.user.UserAuthResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    UserDto findById(Long id);

    UserDto findByEmail(String email);

    UserInfoDto getUserInfo(String email);

    CreateUserDtoResponse create(CreateUserDtoRequest userDtoRequest, RoleType role);

    UserAuthResponseDto authenticate(UserAuthRequestDto userDtoRequest);

    UpdateUserDtoResponse update(UpdateUserDtoRequest userDtoRequest);

    List<UserDto> searchUsers(UserSearchDto userSearchDto);
}
