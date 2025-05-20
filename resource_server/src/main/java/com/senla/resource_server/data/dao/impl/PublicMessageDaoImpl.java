package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.PublicMessageDao;
import com.senla.resource_server.data.entity.PublicMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class PublicMessageDaoImpl implements PublicMessageDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PublicMessage save(PublicMessage message) {
        entityManager.persist(message);
        return message;
    }
}
