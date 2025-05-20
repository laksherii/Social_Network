package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(target = "content", source = "content")
    PrivateMessageResponseDto toPrivateMessageResponse(PrivateMessage message);

    @Mapping(target = "communityName", source = "community.name")
    SendCommunityMessageResponseDto toCommunityMessageResponse(PublicMessage savedMessage);

    @Mapping(target = "name", source = "groupChat.name")
    GroupChatMessageResponseDto toGroupChatMessageResponse(PublicMessage savedMessage);

    @Mapping(target = "owner.email", source = "wall.owner.email")
    @Mapping(target = "content", source = "content")
    WallResponseDto toWallMessageResponse(PublicMessage savedMessage);
}
