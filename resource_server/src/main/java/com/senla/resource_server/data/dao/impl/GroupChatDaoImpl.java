package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GroupChatDaoImpl implements GroupChatDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<GroupChat> findByUser(User user) {
        log.info("Finding group chats for user with ID: {}", user.getId());
        List<GroupChat> groupChats = entityManager.createQuery("""
                            SELECT g FROM GroupChat g
                            JOIN g.users u
                            WHERE u = :user
                        """, GroupChat.class)
                .setParameter("user", user)
                .getResultList();
        log.info("Found {} group chats for user with ID: {}", groupChats.size(), user.getId());
        return groupChats;
    }

    @Override
    public Optional<GroupChat> findById(Long id) {
        log.info("Finding group chat with ID: {}", id);
        GroupChat groupChat = entityManager.find(GroupChat.class, id);
        log.info("Group chat found with ID: {}", id);
        return Optional.ofNullable(groupChat);
    }

    @Override
    public GroupChat save(GroupChat groupChat) {
        log.info("Saving group chat with name: {}", groupChat.getName());
        entityManager.persist(groupChat);
        log.info("Successfully saved group chat with name: {}", groupChat.getName());
        return groupChat;
    }

    @Override
    public PublicMessage sendMessage(PublicMessage publicMessage) {
        log.info("Sending public message with ID: {}", publicMessage.getId());
        entityManager.persist(publicMessage);
        log.info("Successfully sent public message with ID: {}", publicMessage.getId());
        return publicMessage;
    }
}
