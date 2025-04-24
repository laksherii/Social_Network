package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupChatDaoImpl implements GroupChatDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<GroupChat> findByUser(User user) {
        return entityManager.createQuery("""
                SELECT g FROM GroupChat g
                JOIN g.users u
                WHERE u = :user
            """, GroupChat.class)
                .setParameter("user", user)
                .getResultList();
    }

    public GroupChat findById(Long id) {
        return entityManager.find(GroupChat.class, id);
    }

    public GroupChat save(GroupChat groupChat) {
        entityManager.persist(groupChat);
        return groupChat;
    }
}
