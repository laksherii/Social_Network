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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PrivateMessageServiceImpl implements PrivateMessageService {

    private final PrivateMessageDao privateMessageDao;
    private final UserDao userDao;
    private final MessageMapper messageMapper;
    private final AuthService authService;

    @Override
    public PrivateMessageResponseDto sendPrivateMessage(PrivateMessageRequestDto privateMessageRequestDto) {

        User sender = authService.getCurrentUser();

        User recipient = userDao.findByEmail(privateMessageRequestDto.getRecipientEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        PrivateMessage message = PrivateMessage.builder()
                .sender(sender)
                .recipient(recipient)
                .content(privateMessageRequestDto.getMessage())
                .build();

        PrivateMessage savedMessage = privateMessageDao.save(message);
        log.info("Private message successfully sent from {} to {} (Message ID: {})", sender.getEmail(), recipient.getEmail(), savedMessage.getId());

        return messageMapper.toPrivateMessageResponse(savedMessage);
    }

    @Override
    public List<PrivateMessageResponseDto> getMessagesByRecipientEmail(String email) {

        User sender = authService.getCurrentUser();

        User recipient = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<PrivateMessage> privateMessageList = privateMessageDao.findBySenderAndRecipient(sender, recipient);
        log.info("Found {} messages between sender {} and recipient {}", privateMessageList.size(), sender.getEmail(), recipient.getEmail());

        return privateMessageList.stream()
                .map(messageMapper::toPrivateMessageResponse)
                .toList();
    }
}