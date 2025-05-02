package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.message.CreateCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageResponseDto;

public interface CommunityMessageService {
    CreateCommunityMessageResponseDto sendCommunityMessage(CreateCommunityMessageRequestDto createCommunityMessageDto);
}
