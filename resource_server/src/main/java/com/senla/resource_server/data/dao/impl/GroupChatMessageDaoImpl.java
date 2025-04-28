package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.GroupChatMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class GroupChatMessageDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public GroupChatMessage save(GroupChatMessage message) {
        log.info("Saving message with ID: {}", message.getId());
        entityManager.persist(message);
        log.info("Successfully saved message with ID: {}", message.getId());
        return message;
    }

    public List<GroupChatMessage> findByGroup(GroupChat groupChat) {
        log.info("Fetching messages for group chat with ID: {}", groupChat.getId());
        List<GroupChatMessage> messages = entityManager.createQuery("""
            SELECT m FROM GroupChatMessage m
            WHERE m.groupChat = :groupChat
            ORDER BY m.id DESC
            """, GroupChatMessage.class)
                .setParameter("groupChat", groupChat)
                .getResultList();
        log.info("Found {} messages for group chat with ID: {}", messages.size(), groupChat.getId());
        return messages;
    }
}

