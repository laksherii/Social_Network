package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import jakarta.validation.Valid;

public interface GroupChatService {
    CreateGroupChatResponseDto create(CreateGroupChatRequestDto groupDto);

    GroupChatMessageResponseDto sendGroupChatMessage(@Valid GroupChatMessageRequestDto groupChatMessageRequest);

    GroupChatDto findById(Long id);
}
