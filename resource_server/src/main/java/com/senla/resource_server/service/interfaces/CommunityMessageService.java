package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.message.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.SendCommunityMessageResponseDto;

public interface CommunityMessageService {

    SendCommunityMessageResponseDto sendCommunityMessage(SendCommunityMessageRequestDto createCommunityMessageDto);
}
