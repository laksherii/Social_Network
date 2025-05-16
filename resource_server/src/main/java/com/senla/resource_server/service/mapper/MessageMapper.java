package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(target = "content", source = "content")
    PrivateMessageResponseDto toPrivateMessageResponse(PrivateMessage message);

}
