package com.senla.resource_server.data.mapper;

import com.senla.resource_server.data.entity.CommunityMessage;
import com.senla.resource_server.data.entity.GroupChatMessage;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponse;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    PrivateMessageResponseDto toPrivateMessageResponse(PrivateMessage message);

    @Mapping(source = "groupChat.name", target = "name")
    @Mapping(source = "message", target = "message")
    GroupChatMessageResponse toGroupChatMessageResponse(GroupChatMessage message);

    @Mapping(source = "community.name", target = "communityName")
    @Mapping(source = "message", target = "message")
    CreateCommunityMessageResponseDto toCreateCommunityMessageResponseDto(CommunityMessage message);
}
