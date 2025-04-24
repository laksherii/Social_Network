package com.senla.resource_server.data.mapper;

import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityMapper {

    @Mapping(source = "name", target = "communityName")
    CreateCommunityResponseDto toCreateCommunityResponseDto(Community community);
}
