package com.senla.resource_server.service.impl;

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
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PrivateMessageServiceImpl {

    private final PrivateMessageDaoImpl privateMessageDao;
    private final UserDaoImpl userDao;
    private final MessageMapper messageMapper;

    public PrivateMessageResponseDto sendPrivateMessage(PrivateMessageRequestDto privateMessageRequestDto) {
        User sender = userDao.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User recipient = userDao.findById(privateMessageRequestDto.getRecipient())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        PrivateMessage message = new PrivateMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setMessage(privateMessageRequestDto.getMessage());
        message.setRead(false);

        PrivateMessage saved = privateMessageDao.save(message);
        return messageMapper.toPrivateMessageResponse(saved);
    }
}
