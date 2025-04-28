package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import com.senla.resource_server.service.impl.WallServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wall")
public class RestWallController {

    private final WallServiceImpl wallService;

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public WallResponseDto updateWall(@RequestBody WallRequestDto requestDto) {
        log.info("Received request to update wall with data: {}", requestDto);
        WallResponseDto response = wallService.updateWall(requestDto);
        log.info("Successfully updated wall, response: {}", response);
        return response;
    }
}
