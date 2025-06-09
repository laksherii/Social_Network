package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;

public interface CommunityMessageService {

    SendCommunityMessageResponseDto sendCommunityMessage(SendCommunityMessageRequestDto createCommunityMessageDto);
}
