package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupChatMapper {

    CreateGroupChatResponseDto toCreateGroupDtoResponse(GroupChat groupChat);

    @Mapping(target = "messages", source = "messages")
    GroupChatDto toGroupChatDto(GroupChat groupChat);
}
