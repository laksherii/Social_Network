package com.senla.social_network.service.impl;

import com.senla.social_network.data.dao.impl.UserDaoImpl;
import com.senla.social_network.data.entity.PrivateMessage;
import com.senla.social_network.data.entity.User;
import com.senla.social_network.data.entity.User.RoleType;
import com.senla.social_network.service.dto.message.SendPrivateMessageRequest;
import com.senla.social_network.service.dto.message.SendPrivateMessageResponse;
import com.senla.social_network.service.dto.user.CreateUserDtoRequest;
import com.senla.social_network.service.dto.user.CreateUserDtoResponse;
import com.senla.social_network.service.dto.user.UserDto;
import com.senla.social_network.data.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl {

    private final UserDaoImpl userDao;
    private final UserMapper userMapper;

    public UserDto findById(Long id) {
        User byId = userDao.findById(id).orElse(null);
        return userMapper.toUserDto(byId);
    }

    public UserDto findByEmail(String email) {
        User user = userDao.findByEmail(email).orElse(null);
        return userMapper.toUserDto(user);
    }

    public CreateUserDtoResponse create(CreateUserDtoRequest userDtoRequest) {
        if (userDtoRequest.getEmail() == null || userDao.findByEmail(userDtoRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already exist");
        }
        User user = userMapper.toUserCreate(userDtoRequest);
        user.setRole(RoleType.USER);
        user.setEnabled(true);
        userDao.save(user);
        return userMapper.toCreateUserDtoResponse(user);
    }

    public SendPrivateMessageResponse sendPrivateMessage(SendPrivateMessageRequest sendPrivateMessageRequest) {
        User user = userDao.findById(sendPrivateMessageRequest.getRecipient()).orElse(null);
        Set<PrivateMessage> recipientPrivateMessages = user.getRecipientPrivateMessages();
        recipientPrivateMessages.add()
    }
}
