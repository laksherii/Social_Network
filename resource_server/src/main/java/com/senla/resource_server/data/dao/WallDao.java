package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.Wall;

import java.util.Optional;

public interface WallDao {
    Wall save(Wall wall);

    Wall update(Wall wall);

    Optional<Wall> findByEmail(String email);
}
