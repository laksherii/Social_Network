package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.entity.Community;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public Community save(Community community) {
        entityManager.persist(community);
        return community;
    }

    public Community findById(Long id) {
        return entityManager.find(Community.class, id);
    }
}
