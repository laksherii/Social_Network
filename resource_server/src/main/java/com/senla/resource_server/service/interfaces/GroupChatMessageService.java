package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.message.GetGroupChatMessageDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;

import java.util.List;

public interface GroupChatMessageService {
    GroupChatMessageResponseDto sendGroupChatMessage(GroupChatMessageRequestDto groupChatMessage);

    List<GetGroupChatMessageDto> getGroupChatMessages(Long groupId);
}
