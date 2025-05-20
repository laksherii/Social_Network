package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.PrivateMessageDao;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class PrivateMessageDaoImpl implements PrivateMessageDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PrivateMessage save(PrivateMessage message) {
        entityManager.persist(message);
        log.info("Successfully saved private message with ID: {}", message.getId());
        return message;
    }

    @Override
    public List<PrivateMessage> findBySenderAndRecipient(User sender, User recipient) {
        List<PrivateMessage> messages = entityManager.createQuery("""
        SELECT m FROM PrivateMessage m
        WHERE (m.sender = :sender AND m.recipient = :recipient)
           OR (m.sender = :recipient AND m.recipient = :sender)
        ORDER BY m.id DESC
        """, PrivateMessage.class)
                .setParameter("sender", sender)
                .setParameter("recipient", recipient)
                .getResultList();

        log.info("Found {} private messages between users with IDs: {} and {}", messages.size(), sender.getId(), recipient.getId());
        return messages;
    }
}

