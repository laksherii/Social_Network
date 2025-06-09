package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.FriendRequestDao;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FriendRequestDaoImpl implements FriendRequestDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FriendRequest save(FriendRequest request) {
        entityManager.persist(request);
        return request;
    }

    @Override
    public Optional<FriendRequest> findById(Long id) {
        FriendRequest request = entityManager.find(FriendRequest.class, id);
        return Optional.ofNullable(request);
    }

    @Override
    public Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient) {
        String query = "SELECT fr FROM FriendRequest fr WHERE fr.sender = :sender AND fr.recipient = :recipient";
        List<FriendRequest> result = entityManager.createQuery(query, FriendRequest.class)
                .setParameter("sender", sender)
                .setParameter("recipient", recipient)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public void delete(Long id) {
        FriendRequest request = entityManager.find(FriendRequest.class, id);
        entityManager.remove(request);
    }
}
