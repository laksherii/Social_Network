package com.senla.auth_server.data.dao;

import com.senla.auth_server.data.UserDao;
import com.senla.auth_server.data.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByEmail(String email) {
        String jpql = """
                select u
                from User u
                where u.email = :email
                and u.enabled = true
            """;
        List<User> users = entityManager.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getResultList();

        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            log.info("User found with email: {}", email);
            return users.stream().findFirst();
        }
    }

    @Override
    public User save(User user) {
        entityManager.persist(user);
        log.info("User successfully saved: id={}, email={}", user.getId(), user.getEmail());
        return user;
    }
}
