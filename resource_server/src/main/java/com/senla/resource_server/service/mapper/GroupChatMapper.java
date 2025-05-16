package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.groupChat.GroupResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupChatMapper {

    CreateGroupChatResponseDto toCreateGroupDtoResponse(GroupChat groupChat);

    @Mapping(target = "name", source = "groupChat.name")
    @Mapping(target = "content", source = "content")
    GroupChatMessageResponseDto toGroupChatMessageResponseDto(PublicMessage publicMessage);

    @Mapping(target = "messages", source = "messages")
    GroupChatDto toGroupChatDto(GroupChat groupChat);
}
