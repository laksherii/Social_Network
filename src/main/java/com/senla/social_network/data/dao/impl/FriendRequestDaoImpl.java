package com.senla.social_network.data.dao.impl;

import com.senla.social_network.data.dao.FriendRequestDao;
import com.senla.social_network.data.entity.FriendRequest;
import com.senla.social_network.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendRequestDaoImpl implements FriendRequestDao {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void save(FriendRequest request) {
        if (request.getId() == null) {
            entityManager.persist(request);
        } else {
            entityManager.merge(request);
        }
    }

    @Override
    public boolean existsBySenderAndRecipient(User sender, User recipient) {
        String jpql = "SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.sender = :sender AND fr.recipient = :recipient";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("sender", sender)
                .setParameter("recipient", recipient)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<FriendRequest> findById(Long id) {
        FriendRequest request = entityManager.find(FriendRequest.class, id);
        return Optional.ofNullable(request);
    }

    @Override
    public Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient) {
        String jpql = "SELECT fr FROM FriendRequest fr WHERE fr.sender = :sender AND fr.recipient = :recipient";
        List<FriendRequest> result = entityManager.createQuery(jpql, FriendRequest.class)
                .setParameter("sender", sender)
                .setParameter("recipient", recipient)
                .getResultList();
        return result.stream().findFirst();
    }
}
