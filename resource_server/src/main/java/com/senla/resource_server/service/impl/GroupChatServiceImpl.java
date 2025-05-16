package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.interfaces.GroupChatService;
import com.senla.resource_server.service.mapper.GroupChatMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    private final GroupChatDao groupChatDao;

    @Override
    public CreateGroupChatResponseDto create(CreateGroupChatRequestDto groupDto) {
        log.info("Starting group chat creation with name: {}", groupDto.getName());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        log.info("Authenticated sender: {}", senderEmail);

        groupDto.getUserEmails().add(senderEmail);
        log.info("Added authenticated user to the group. Total user IDs: {}", groupDto.getUserEmails().size());

        Set<User> users = groupDto.getUserEmails().stream()
                .map(userDao::findByEmail)
                .filter(Optional::isPresent)
                .distinct()
                .map(Optional::get)
                .collect(Collectors.toSet());

        log.info("Successfully fetched {} users for the group chat", users.size());

        GroupChat groupChat = new GroupChat();
        groupChat.setUsers(users);
        groupChat.setName(groupDto.getName());

        GroupChat savedGroupChat = groupDao.save(groupChat);
        log.info("Group chat created successfully with ID: {}", savedGroupChat.getId());

        return groupChatMapper.toCreateGroupDtoResponse(savedGroupChat);
    }

    @Override
    public GroupChatMessageResponseDto sendGroupChatMessage(GroupChatMessageRequestDto groupChatMessageRequest) {
        log.info("Starting sending group chat message");

        log.info("Retrieving authentication from security context");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        log.info("Authenticated sender: {}", senderEmail);

        log.info("Fetching group chat with ID: {}", groupChatMessageRequest.getGroupId());
        GroupChat groupChat = groupDao.findById(groupChatMessageRequest.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
        log.info("Group chat found: ID = {}", groupChat.getId());

        log.info("Verifying that sender is a member of the group chat");
        groupChat.getUsers().stream()
                .map(User::getEmail)
                .filter(e -> e.equals(senderEmail))
                .findFirst()
                .orElseThrow(() -> new UserNotInGroupChatException("User not in group chat"));
        log.info("Sender is confirmed to be in the group chat");

        log.info("Fetching user entity by email: {}", senderEmail);
        User user = userDao.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("User entity found: ID = {}", user.getId());

        log.info("Building public message entity");
        PublicMessage publicMessage = PublicMessage.builder()
                .groupChat(groupChat)
                .content(groupChatMessageRequest.getMessage())
                .sender(user)
                .build();
        log.info("Public message built successfully");

        log.info("Persisting public message to database");
        PublicMessage message = groupChatDao.sendMessage(publicMessage);
        log.info("Successfully sent message to the group chat with ID: {}", message.getId());

        log.info("Mapping public message to response DTO");
        return groupChatMapper.toGroupChatMessageResponseDto(message);
    }

    @Override
    public GroupChatDto findById(Long id) {
        log.info("Starting group chat find by ID: {}", id);
        GroupChat groupChat = groupDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
        groupChatMapper.toGroupChatDto(groupChat);
        log.info("Group chat found: ID = {}", groupChat.getId());
        return groupChatMapper.toGroupChatDto(groupChat);
    }
}
