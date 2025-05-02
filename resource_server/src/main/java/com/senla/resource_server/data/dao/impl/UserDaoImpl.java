package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findById(Long id) {
        log.info("Searching for user with ID: {}", id);
        User user = entityManager.find(User.class, id);
        log.info("User found with ID: {}", id);
        return Optional.ofNullable(user);
    }

    @Override
    public User save(User user) {
        log.info("Saving user with email: {}", user.getEmail());
        entityManager.persist(user);
        log.info("User saved with email: {}", user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Searching for user with email: {}", email);
        List<User> users = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return users.stream().findFirst();
    }

    @Override
    public User update(User user) {
        log.info("Updating user with ID: {}", user.getId());
        User result = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", user.getId())
                .getSingleResult();

        entityManager.detach(result);
        User updatedUser = entityManager.merge(user);
        log.info("User updated with ID: {}", user.getId());
        return updatedUser;
    }

    @Override
    public List<User> searchUser(UserSearchDto userSearchDto) {
        log.info("Searching for users with criteria: {}", userSearchDto);
        StringBuilder jpql = new StringBuilder("SELECT u FROM User u WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (userSearchDto.getFirstName() != null && !userSearchDto.getFirstName().isEmpty()) {
            jpql.append(" AND LOWER(u.firstName) LIKE :firstName");
            parameters.put("firstName", "%" + userSearchDto.getFirstName().toLowerCase() + "%");
        }

        if (userSearchDto.getLastName() != null && !userSearchDto.getLastName().isEmpty()) {
            jpql.append(" AND LOWER(u.lastName) LIKE :lastName ");
            parameters.put("lastName", "%" + userSearchDto.getLastName().toLowerCase() + "%");
        }

        if (userSearchDto.getAge() != null) {
            jpql.append("""
                    AND (YEAR(CURRENT_DATE) - YEAR(u.birthDay) -
                        CASE
                            WHEN MONTH(CURRENT_DATE) < MONTH(u.birthDay)
                                 OR (MONTH(CURRENT_DATE) = MONTH(u.birthDay)
                                 AND DAY(CURRENT_DATE) < DAY(u.birthDay))
                            THEN 1
                            ELSE 0
                        END) = :age
                    """);
            parameters.put("age", userSearchDto.getAge());
        }

        if (userSearchDto.getGender() != null) {
            jpql.append(" AND u.gender = :gender");
            parameters.put("gender", userSearchDto.getGender());
        }

        jpql.append(" ORDER BY u.lastName ");
        TypedQuery<User> query = entityManager.createQuery(jpql.toString(), User.class);

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        List<User> result = query.getResultList();
        log.info("Found {} users matching the search criteria.", result.size());
        return result;
    }
}

