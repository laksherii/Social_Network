package com.senla.social_network.data.mapper;


import com.senla.social_network.data.entity.User;
import com.senla.social_network.service.dto.user.CreateUserDtoRequest;
import com.senla.social_network.service.dto.user.CreateUserDtoResponse;
import com.senla.social_network.service.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "email", target = "email")
    UserDto toUserDto(User user);

    User toUserCreate(CreateUserDtoRequest createUserDtoRequest);

    CreateUserDtoResponse toCreateUserDtoResponse(User user);
}
