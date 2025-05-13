package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.dao.GroupChatMessageDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.GroupChatMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.service.dto.message.GetGroupChatMessageDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.interfaces.GroupChatMessageService;
import com.senla.resource_server.service.mapper.MessageMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupChatMessageServiceImpl implements GroupChatMessageService {

    private final GroupChatMessageDao groupChatMessageDao;
    private final GroupChatDao groupChatDao;
    private final UserDao userDaoImpl;
    private final MessageMapper messageMapper;

    @Override
    public GroupChatMessageResponseDto sendGroupChatMessage(GroupChatMessageRequestDto groupChatMessage) {
        log.info("Starting to send group chat message to group ID: {}", groupChatMessage.getGroupId());


        GroupChat groupChat = groupChatDao.findById(groupChatMessage.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("GroupChat not found"));
        log.info("Group chat found with ID: {}", groupChat.getId());

        //todo проверить
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        log.info("Authenticated sender: {}", senderEmail);

        User user = userDaoImpl.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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

    @Override
    public List<GetGroupChatMessageDto> getGroupChatMessages(Long groupId) {
        log.info("Retrieving messages for group chat ID: {}", groupId);

        //todo проверить
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        log.info("Authenticated sender: {}", senderEmail);

        GroupChat groupChat = groupChatDao.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("GroupChat not found"));
        log.info("Group chat found with ID: {}", groupChat.getId());

        User user = userDaoImpl.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("No such user"));
        log.info("User found with ID: {}", user.getId());

        if (!groupChat.getUsers().contains(user)) {
            log.info("User ID {} is not a participant of group chat ID {}", user.getId(), groupChat.getId());
            throw new UserNotInGroupChatException("User not in group chat");
        }

        List<GetGroupChatMessageDto> messages = groupChat.getMessages().stream()
                .map(messageMapper::toGetGroupChatMessage)
                .toList();

        log.info("Retrieved {} messages for group chat ID: {}", messages.size(), groupChat.getId());
        return messages;
    }
}