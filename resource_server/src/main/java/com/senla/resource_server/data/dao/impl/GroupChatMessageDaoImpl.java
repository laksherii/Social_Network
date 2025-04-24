package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.GroupChatMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupChatMessageDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public GroupChatMessage save(GroupChatMessage message) {
        entityManager.persist(message);
        return message;
    }

    public List<GroupChatMessage> findByGroup(GroupChat groupChat) {
        return entityManager.createQuery("""
            SELECT m FROM GroupChatMessage m
            WHERE m.groupChat = :groupChat
            ORDER BY m.id DESC
            """, GroupChatMessage.class)
                .setParameter("groupChat", groupChat)
                .getResultList();
    }
}
