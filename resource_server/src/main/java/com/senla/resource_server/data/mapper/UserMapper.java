package com.senla.resource_server.data.mapper;


import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserAuthResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "email", target = "email")
    UserDto toUserDto(User user);

    UserInfoDto toUserInfoDto(User user);

    User toUser(UserDto userDto);

    @Mapping(target = "role", constant = "ROLE_USER")
    User toUserCreate(CreateUserDtoRequest createUserDtoRequest);

    CreateUserDtoResponse toCreateUserDtoResponse(User user);

    @Mapping(source = ".", target = "user")
    UpdateUserDtoResponse updateUserResponseToDto(User user);

    UserAuthResponseDto toUserAuthResponseDto(User user);
}
