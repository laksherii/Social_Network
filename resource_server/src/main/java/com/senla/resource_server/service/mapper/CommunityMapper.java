package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityMapper {

    @Mapping(source = "name", target = "communityName")
    CreateCommunityResponseDto toCreateCommunityResponseDto(Community community);

    @Mapping(source = "name", target = "communityName")
    @Mapping(source = "description", target = "description")
    JoinCommunityResponseDto toJoinCommunityResponseDto(Community community);

    CommunityDto toCommunityDto(Community community);

    @Mapping(target = "communityName", source = "community.name")
    SendCommunityMessageResponseDto toSendCommunityMessageResponseDto(PublicMessage publicMessage);
}
