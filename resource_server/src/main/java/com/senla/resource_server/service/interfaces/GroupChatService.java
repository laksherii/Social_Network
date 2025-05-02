package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;

public interface GroupChatService {
    CreateGroupChatResponseDto create(CreateGroupChatRequestDto groupDto);
}
