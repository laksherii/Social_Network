package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.user.UserSearchDto;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);

    User save(User user);

    Optional<User> findByEmail(String email);

    User update(User user);

    List<User> searchUser(UserSearchDto userSearchDto);
}
