package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.WallDao;
import com.senla.resource_server.data.entity.Wall;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class WallDaoImpl implements WallDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Wall save(Wall wall) {
        entityManager.persist(wall);
        return wall;
    }
}
