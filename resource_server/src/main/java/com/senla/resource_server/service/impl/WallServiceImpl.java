package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.dao.impl.WallDaoImpl;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.data.entity.WallMessage;
import com.senla.resource_server.data.mapper.WallMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WallServiceImpl {

    private final WallDaoImpl wallDao;
    private final WallMapper wallMapper;
    private final UserDaoImpl userDaoImpl;

    public WallResponseDto updateWall(WallRequestDto wallRequestDto) {
        log.info("Updating wall with new message");

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Authenticated user email: {}", name);

        User user = userDaoImpl.findByEmail(name)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("User found by email: {}", user.getEmail());

        Wall wall = user.getWall();

        WallMessage message = new WallMessage();
        message.setWall(wall);
        message.setSender(user);
        message.setMessage(wallRequestDto.getMessage());

        wall.getMessages().add(message);

        Wall updatedWall = wallDao.update(wall);

        log.info("Wall updated successfully for user ID: {}", user.getId());

        return wallMapper.toWallResponseDto(updatedWall);
    }
}
