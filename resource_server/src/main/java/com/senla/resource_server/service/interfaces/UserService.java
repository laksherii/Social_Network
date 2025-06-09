package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import com.senla.resource_server.service.dto.user.UserSearchDtoResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    UserDto findById(Long id);

    UserDto findByEmail(String email);

    UserInfoDto getUserInfo(String email);

    CreateUserDtoResponse create(CreateUserDtoRequest userDtoRequest);

    UserSearchDtoResponse update(UpdateUserDtoRequest userDtoRequest);

    List<UserSearchDtoResponse> searchUsers(UserSearchDto userSearchDto);

    UpdateRoleUserDtoResponse updateRole(@Valid UpdateRoleUserDtoRequest updateRoleUserDtoRequest);
}
