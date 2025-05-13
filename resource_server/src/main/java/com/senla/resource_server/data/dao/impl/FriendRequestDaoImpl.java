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
        log.info("Saving friend request from {} to {}", request.getSender().getEmail(), request.getRecipient().getEmail());
        entityManager.persist(request);
        log.info("Successfully saved friend request from {} to {}", request.getSender().getEmail(), request.getRecipient().getEmail());
        return request;
    }

    @Override
    public Optional<FriendRequest> findById(Long id) {
        log.info("Finding friend request by ID: {}", id);
        FriendRequest request = entityManager.find(FriendRequest.class, id);
        log.info("Friend request found with ID: {}", id);
        return Optional.ofNullable(request);
    }

    @Override
    public Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient) {
        log.info("Finding friend request from {} to {}", sender.getEmail(), recipient.getEmail());
        String query = "SELECT fr FROM FriendRequest fr WHERE fr.sender = :sender AND fr.recipient = :recipient";
        List<FriendRequest> result = entityManager.createQuery(query, FriendRequest.class)
                .setParameter("sender", sender)
                .setParameter("recipient", recipient)
                .getResultList();
        if (result.isEmpty()) {
            log.warn("No friend request found from {} to {}", sender.getEmail(), recipient.getEmail());
        } else {
            log.info("Friend request found from {} to {}", sender.getEmail(), recipient.getEmail());
        }
        return result.stream().findFirst();
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting friend request with ID: {}", id);
        FriendRequest request = entityManager.find(FriendRequest.class, id);
        entityManager.remove(request);
        log.info("Deleted friend request from {} to {}", request.getSender().getEmail(), request.getRecipient().getEmail());
    }
}
