package com.senla.auth_server.service.mapper;

import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.service.dto.UserDtoRequest;
import com.senla.auth_server.service.dto.UserDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toUserFromUserDtoRequest(UserDtoRequest userDto);

    UserDtoResponse toUserDtoResponse(User user);
}

