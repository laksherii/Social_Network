package com.senla.resource_server.service.impl;

import com.senla.resource_server.config.UserIdAuthenticationToken;
import com.senla.resource_server.data.dao.impl.GroupChatDaoImpl;
import com.senla.resource_server.data.dao.impl.GroupChatMessageDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.GroupChatMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.mapper.MessageMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupChatMessageServiceImpl {

    private final GroupChatMessageDaoImpl groupChatMessageDao;
    private final GroupChatDaoImpl groupChatDaoImpl;
    private final UserDaoImpl userDaoImpl;
    private final MessageMapper messageMapper;

    public GroupChatMessageResponseDto sendGroupChatMessage(GroupChatMessageRequestDto groupChatMessage) {
        log.info("Starting to send group chat message to group ID: {}", groupChatMessage.getGroupId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserIdAuthenticationToken) authentication).getUserId();
        log.info("Authenticated sender user ID: {}", userId);

        GroupChat groupChat = groupChatDaoImpl.findById(groupChatMessage.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("GroupChat not found"));
        log.info("Group chat found with ID: {}", groupChat.getId());

        User user = userDaoImpl.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No such user"));

        if (!groupChat.getUsers().contains(user)) {
            log.info("User ID {} is not a participant of group chat ID {}", user.getId(), groupChat.getId());
            throw new UserNotInGroupChatException("User not in group chat");
        }

        GroupChatMessage groupMessage = new GroupChatMessage();
        groupMessage.setMessage(groupChatMessage.getMessage());
        groupMessage.setSender(user);
        groupMessage.setGroupChat(groupChat);

        groupChat.getMessages().add(groupMessage);
        groupChat.setMessages(groupChat.getMessages());

        GroupChatMessage savedMessage = groupChatMessageDao.save(groupMessage);
        log.info("Group chat message saved. Message ID: {}, Group ID: {}, Sender ID: {}", savedMessage.getId(), groupChat.getId(), user.getId());

        return messageMapper.toGroupChatMessageResponse(savedMessage);
    }

    public List<GroupChatMessageResponseDto> getGroupChatMessages(Long groupId) {
        log.info("Retrieving messages for group chat ID: {}", groupId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserIdAuthenticationToken) authentication).getUserId();
        log.info("Authenticated user ID: {}", userId);

        GroupChat groupChat = groupChatDaoImpl.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("GroupChat not found"));
        log.info("Group chat found with ID: {}", groupChat.getId());

        User user = userDaoImpl.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No such user"));
        log.info("User found with ID: {}", user.getId());

        if (!groupChat.getUsers().contains(user)) {
            log.info("User ID {} is not a participant of group chat ID {}", user.getId(), groupChat.getId());
            throw new UserNotInGroupChatException("User not in group chat");
        }

        List<GroupChatMessageResponseDto> messages = groupChat.getMessages().stream()
                .map(messageMapper::toGroupChatMessageResponse)
                .sorted()
                .toList();

        log.info("Retrieved {} messages for group chat ID: {}", messages.size(), groupChat.getId());
        return messages;
    }
}