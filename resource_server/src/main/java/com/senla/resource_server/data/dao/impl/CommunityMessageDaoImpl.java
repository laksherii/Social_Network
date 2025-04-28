package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.entity.CommunityMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CommunityMessageDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public CommunityMessage save(CommunityMessage communityMessage) {
        log.info("Saving community message with ID {}", communityMessage.getId());
        entityManager.persist(communityMessage);
        log.info("Successfully saved community message with ID {}", communityMessage.getId());
        return communityMessage;
    }
}
