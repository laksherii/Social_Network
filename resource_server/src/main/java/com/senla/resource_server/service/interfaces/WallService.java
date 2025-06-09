package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;

public interface WallService {
    WallResponseDto updateWall(WallRequestDto wallRequestDto);
}
