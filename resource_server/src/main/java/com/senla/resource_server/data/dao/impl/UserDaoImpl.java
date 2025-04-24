package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.entity.User;
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
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
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

    public User update(User user) {
        User result = entityManager.createQuery(
                "SELECT u FROM User u " +
                "WHERE u.id = :id", User.class)
                .setParameter("id", user.getId())
                .getSingleResult();

        entityManager.detach(result);
        return entityManager.merge(user);
    }

}
