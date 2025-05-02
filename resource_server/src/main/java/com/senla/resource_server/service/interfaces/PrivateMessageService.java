package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;

import java.util.List;

public interface PrivateMessageService {
    PrivateMessageResponseDto sendPrivateMessage(PrivateMessageRequestDto privateMessageRequestDto);

    List<PrivateMessageResponseDto> getMessagesByRecipientEmail(String email);
}
