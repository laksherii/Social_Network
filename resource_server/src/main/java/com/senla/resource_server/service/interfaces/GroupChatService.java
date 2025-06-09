package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;

public interface GroupChatService {
    CreateGroupChatResponseDto create(CreateGroupChatRequestDto groupDto);

    GroupChatDto findById(Long id);
}
