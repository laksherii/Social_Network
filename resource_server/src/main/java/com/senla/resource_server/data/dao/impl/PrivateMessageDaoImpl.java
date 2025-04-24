package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PrivateMessageDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public PrivateMessage save(PrivateMessage message) {
        entityManager.persist(message);
        return message;
    }

    public List<PrivateMessage> findByRecipient(User recipient) {
        return entityManager.createQuery("""
            SELECT m FROM PrivateMessage m
            WHERE m.recipient = :recipient
            ORDER BY m.id DESC
            """, PrivateMessage.class)
                .setParameter("recipient", recipient)
                .getResultList();
    }
}
