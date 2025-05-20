package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserDao userDao;

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Current authenticated user: {} ({})", user.getFirstName(), user.getEmail());
        return user;
    }
}

