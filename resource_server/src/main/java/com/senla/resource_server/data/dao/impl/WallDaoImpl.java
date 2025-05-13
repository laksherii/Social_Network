package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.WallDao;
import com.senla.resource_server.data.entity.Wall;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class WallDaoImpl implements WallDao {
//todo доработать
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Wall save(Wall wall) {
//        log.info("Saving wall for user with email: {}", wall.getOwner().getEmail());
        entityManager.persist(wall);
//        log.info("Wall saved for user with email: {}", wall.getOwner().getEmail());
        return wall;
    }

    @Override
    public Wall update(Wall wall) {
//        log.info("Updating wall for user with email: {}", wall.getOwner().getEmail());
        Wall updatedWall = entityManager.merge(wall);
//        log.info("Wall updated for user with email: {}", wall.getOwner().getEmail());
        return updatedWall;
    }

//    @Override
//    public Optional<Wall> findByEmail(String email) {
//        log.info("Searching for wall by email: {}", email);
//        List<Wall> walls = entityManager.createQuery(
//                        "SELECT w FROM Wall w WHERE w.owner.email = :email",
//                        Wall.class)
//                .setParameter("email", email)
//                .getResultList();
//        return walls.stream().findFirst();
//    }
}
