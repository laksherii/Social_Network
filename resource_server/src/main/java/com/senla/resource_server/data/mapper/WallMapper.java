package com.senla.resource_server.data.mapper;

import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.data.entity.WallMessage;
import com.senla.resource_server.service.dto.message.WallMessageDto;
import com.senla.resource_server.service.dto.wall.WallDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WallMapper {


    WallResponseDto toWallResponseDto(Wall wall);

    WallDto toWallDto(Wall wall);

    WallMessageDto toWallMessageDto(WallMessage wallMessage);
}
