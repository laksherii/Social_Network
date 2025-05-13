package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
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

    @Override
    public CreateGroupChatResponseDto create(CreateGroupChatRequestDto groupDto) {
        log.info("Starting group chat creation with name: {}", groupDto.getName());

        //todo проверить
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
}
