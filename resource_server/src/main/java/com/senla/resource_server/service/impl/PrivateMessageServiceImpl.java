package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.PrivateMessageDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.interfaces.PrivateMessageService;
import com.senla.resource_server.service.mapper.MessageMapper;
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
public class PrivateMessageServiceImpl implements PrivateMessageService {

    private final PrivateMessageDao privateMessageDao;
    private final UserDao userDao;
    private final MessageMapper messageMapper;

    @Override
    public PrivateMessageResponseDto sendPrivateMessage(PrivateMessageRequestDto privateMessageRequestDto) {
        log.info("Starting to send private message to user ID: {}", privateMessageRequestDto.getRecipientEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        log.info("Authenticated sender: {}", senderEmail);

        User sender = userDao.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User recipient = userDao.findByEmail(privateMessageRequestDto.getRecipientEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Authenticated recipient ID: {}", recipient.getId());

        PrivateMessage message = new PrivateMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(privateMessageRequestDto.getMessage());

        PrivateMessage savedMessage = privateMessageDao.save(message);
        log.info("Private message successfully sent from {} to {} (Message ID: {})", sender.getEmail(), recipient.getEmail(), savedMessage.getId());

        return messageMapper.toPrivateMessageResponse(savedMessage);
    }

    @Override
    public List<PrivateMessageResponseDto> getMessagesByRecipientEmail(String email) {
        log.info("Fetching messages by recipient email: {}", email);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        log.info("Authenticated sender: {}", senderEmail);

        User sender = userDao.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Sender found by Email: {}", sender.getEmail());

        User recipient = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Recipient found by Email: {}", recipient.getEmail());

        List<PrivateMessage> privateMessageList = privateMessageDao.findBySenderAndRecipient(sender, recipient);
        log.info("Found {} messages between sender {} and recipient {}", privateMessageList.size(), sender.getEmail(), recipient.getEmail());

        return privateMessageList.stream()
                .map(messageMapper::toPrivateMessageResponse)
                .toList();
    }
}