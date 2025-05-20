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
        String jpql = """
                select u
                from User u
                  left join fetch u.wall
                where u.id = :id
            """;
        List<User> users = entityManager.createQuery(jpql, User.class)
                .setParameter("id", id)
                .getResultList();

        if (!users.isEmpty()) {
            log.info("User found with id: {}", id);
        }

        return users.isEmpty() ? Optional.empty() : users.stream().findFirst();
    }

    @Override
    public User save(User user) {
        entityManager.persist(user);
        log.info("User saved with email: {}", user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String jpql = """
                select u
                from User u
                  left join fetch u.wall
                where u.email = :email
            """;
        List<User> users = entityManager.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getResultList();

        if (!users.isEmpty()) {
            log.info("User found with email: {}", email);
        }

        return users.isEmpty() ? Optional.empty() : users.stream().findFirst();
    }


    @Override
    public User update(User user) {
        String jpql = """
                    select u
                    from User u
                      left join fetch u.wall
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

    @Override
    public List<User> searchUser(UserSearchDto userSearchDto) {
        StringBuilder jpql = new StringBuilder("SELECT u FROM User u LEFT JOIN FETCH u.wall WHERE 1=1");
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

