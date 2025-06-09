package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendRequestMapper {

    SendFriendResponseDto toSendFriendResponseDto(FriendRequest friendRequest);

    AnswerFriendResponseDto toAnswerFriendResponseDto(FriendRequest friendRequest);

}
