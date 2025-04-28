package com.senla.resource_server.service.impl;

import com.senla.resource_server.config.UserIdAuthenticationToken;
import com.senla.resource_server.data.dao.impl.PrivateMessageDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.mapper.MessageMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PrivateMessageServiceImpl {

    private final PrivateMessageDaoImpl privateMessageDao;
    private final UserDaoImpl userDao;
    private final MessageMapper messageMapper;

    public PrivateMessageResponseDto sendPrivateMessage(PrivateMessageRequestDto privateMessageRequestDto) {
        log.info("Starting to send private message to user ID: {}", privateMessageRequestDto.getRecipient());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserIdAuthenticationToken) authentication).getUserId();
        log.info("Authenticated sender ID: {}", userId);

        User sender = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User recipient = userDao.findById(privateMessageRequestDto.getRecipient())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Authenticated recipient ID: {}", recipient.getId());

        PrivateMessage message = new PrivateMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setMessage(privateMessageRequestDto.getMessage());
        message.setRead(false);

        PrivateMessage savedMessage = privateMessageDao.save(message);
        log.info("Private message successfully sent from {} to {} (Message ID: {})", sender.getEmail(), recipient.getEmail(), savedMessage.getId());

        return messageMapper.toPrivateMessageResponse(savedMessage);
    }

    public List<PrivateMessageResponseDto> getMessagesByRecipientEmail(String email) {
        log.info("Fetching messages by recipient email: {}", email);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserIdAuthenticationToken) authentication).getUserId();
        log.info("Authenticated sender ID: {}", userId);

        User sender = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Sender found by ID: {}", userId);

        User recipient = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Recipient found by ID: {}", userId);

        List<PrivateMessage> privateMessageList = privateMessageDao.findBySenderAndRecipient(sender, recipient);
        log.info("Found {} messages between sender {} and recipient {}", privateMessageList.size(), sender.getEmail(), recipient.getEmail());

        return privateMessageList.stream()
                .map(messageMapper::toPrivateMessageResponse)
                .toList();
    }
}