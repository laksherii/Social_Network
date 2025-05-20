package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.entity.GroupChat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class GroupChatDaoImpl implements GroupChatDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<GroupChat> findById(Long id) {
        GroupChat groupChat = entityManager.find(GroupChat.class, id);
        if (groupChat != null) {
            log.info("Group chat found with ID: {}", id);
        }
        return Optional.ofNullable(groupChat);
    }

    @Override
    public GroupChat save(GroupChat groupChat) {
        entityManager.persist(groupChat);
        log.info("Successfully saved group chat with name: {}", groupChat.getName());
        return groupChat;
    }
}

