package com.senla.social_network.data.dao.impl;

import com.senla.social_network.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findById(Long id) {
        List<User> users = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.id = :id",
                        User.class)
                .setParameter("id", id)
                .getResultList();
        return users.stream().findFirst();
    }

    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        List<User> users = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email",
                        User.class)
                .setParameter("email", email)
                .getResultList();
        return users.stream().findFirst();
    }

}
