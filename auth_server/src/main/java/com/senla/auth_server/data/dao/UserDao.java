package com.senla.auth_server.data.dao;

import com.senla.auth_server.data.entity.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
}
