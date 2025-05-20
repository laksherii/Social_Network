package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;

public interface PublicMessageService {

    SendCommunityMessageResponseDto sendCommunityMessage(SendCommunityMessageRequestDto requestDto);

    GroupChatMessageResponseDto sendGroupChatMessage(GroupChatMessageRequestDto requestDto);

    WallResponseDto sendWallMessage(WallRequestDto requestDto);
}
