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
                """;
        List<User> users = entityManager.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getResultList();
        return users.isEmpty() ? Optional.empty() : users.stream().findFirst();
    }

    @Override
    public User save(User user) {
        log.info("Saving user with email: {}", user.getEmail());
        entityManager.persist(user);
        log.info("User saved with email: {}", user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Updating user with ID: {}", user.getId());
        String jpql = """
                    select u
                    from User u
                    where u.id = :id
                """;
        User result = entityManager.createQuery(jpql, User.class)
                .setParameter("id", user.getId())
                .getSingleResult();

        entityManager.detach(result);
        User updatedUser = entityManager.merge(user);
        log.info("User updated with ID: {}", user.getId());
        return updatedUser;
    }
}
