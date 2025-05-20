package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.interfaces.GroupChatService;
import com.senla.resource_server.service.mapper.GroupChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupChatServiceImpl implements GroupChatService {

    private final GroupChatDao groupDao;
    private final UserDao userDao;
    private final GroupChatMapper groupChatMapper;
    private final AuthService authService;

    @Override
    public CreateGroupChatResponseDto create(CreateGroupChatRequestDto groupDto) {
        User currentUser = authService.getCurrentUser();

        groupDto.getUserEmails().add(currentUser.getEmail());

        Set<User> users = groupDto.getUserEmails().stream()
                .map(userDao::findByEmail)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());


        GroupChat groupChat = new GroupChat();
        groupChat.setUsers(users);
        groupChat.setName(groupDto.getName());

        GroupChat savedGroupChat = groupDao.save(groupChat);
        log.info("Group chat created successfully with ID: {}", savedGroupChat.getId());

        return groupChatMapper.toCreateGroupDtoResponse(savedGroupChat);
    }

    @Override
    public GroupChatDto findById(Long id) {
        User currentUser = authService.getCurrentUser();

        GroupChat groupChat = groupDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        groupChat.getUsers().stream()
                .map(User::getEmail)
                .filter(email -> email.equals(currentUser.getEmail()))
                .findFirst()
                .orElseThrow(() -> new UserNotInGroupChatException("User not exist in group-chat"));

        log.info("Group Chat found with id {}", id);
        return groupChatMapper.toGroupChatDto(groupChat);
    }
}
