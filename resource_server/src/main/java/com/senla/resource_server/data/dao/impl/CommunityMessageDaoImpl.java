package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.entity.CommunityMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityMessageDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public CommunityMessage save(CommunityMessage communityMessage) {
         entityManager.persist(communityMessage);
         return communityMessage;
    }
}
