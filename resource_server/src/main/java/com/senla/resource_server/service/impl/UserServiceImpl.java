package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import com.senla.resource_server.service.dto.user.UserSearchDtoResponse;
import com.senla.resource_server.service.interfaces.UserService;
import com.senla.resource_server.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final AuthService authService;
    private final UserMapper userMapper;

    @Override
    public UserDto findById(Long id) {
        User byId = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        log.info("User fetched successfully: {} (ID: {})", byId.getEmail(), byId.getId());
        return userMapper.toUserDto(byId);
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
        log.info("User fetched successfully: {} (ID: {})", user.getEmail(), user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserInfoDto getUserInfo(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
        log.info("User info fetched successfully: {} (ID: {})", user.getEmail(), user.getId());
        return userMapper.toUserInfoDto(user);
    }

    @Override
    public CreateUserDtoResponse create(CreateUserDtoRequest userDtoRequest) {

        if (userDao.findByEmail(userDtoRequest.getEmail()).isPresent()) {
            throw new EntityExistException("Email is already exist");
        }

        User user = userMapper.toUserCreate(userDtoRequest);

        user.setEnabled(true);
        user.setRole(RoleType.ROLE_USER);

        Wall wall = new Wall();
        user.setWall(wall);
        wall.setOwner(user);

        User saved = userDao.save(user);
        log.info("User successfully created and saved: {} (ID: {})", saved.getEmail(), saved.getId());

        return userMapper.toCreateUserDtoResponse(saved);
    }

    @Override
    public UserSearchDtoResponse update(UpdateUserDtoRequest userDtoRequest) {
        User user = authService.getCurrentUser();

        if (userDtoRequest.getFirstName() != null) {
            user.setFirstName(userDtoRequest.getFirstName());
        }

        if (userDtoRequest.getLastName() != null) {
            user.setLastName(userDtoRequest.getLastName());
        }

        if (userDtoRequest.getGender() != null) {
            user.setGender(userDtoRequest.getGender());
        }

        if (userDtoRequest.getBirthDay() != null) {
            user.setBirthDay(userDtoRequest.getBirthDay());
        }

        User updatedUser = userDao.update(user);
        log.info("User updated successfully: {} (ID: {})", updatedUser.getEmail(), updatedUser.getId());

        return userMapper.toUserSearchDtoResponse(updatedUser);
    }

    @Override
    public List<UserSearchDtoResponse> searchUsers(UserSearchDto userSearchDto) {
        List<User> users = userDao.searchUser(userSearchDto);
        log.info("Users found: {}", users.size());
        return users.stream()
                .map(userMapper::toUserSearchDtoResponse)
                .toList();
    }

    @Override
    public UpdateRoleUserDtoResponse updateRole(UpdateRoleUserDtoRequest updateRoleUserDtoRequest) {
        User user = userDao.findByEmail(updateRoleUserDtoRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found by email=" + updateRoleUserDtoRequest.getEmail()));

        user.setRole(updateRoleUserDtoRequest.getRole());

        User updatedUser = userDao.update(user);

        UpdateRoleUserDtoResponse updateUserDtoResponse = userMapper.toUpdateRoleUserDtoResponse(updatedUser);
        log.info("Mapped updated user to response DTO");

        return updateUserDtoResponse;
    }
}