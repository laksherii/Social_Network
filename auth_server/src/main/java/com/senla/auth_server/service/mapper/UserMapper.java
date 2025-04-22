package com.senla.auth_server.service.mapper;

import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.service.dto.UserDto;
import com.senla.auth_server.service.dto.UserDtoToResourceServer;
import com.senla.auth_server.service.dto.UserRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "enabled", constant = "true")
    User toUser(UserRegistrationRequest request);

    UserDtoToResourceServer toUserDtoToResourceServer(User user);
}

