package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityRequestDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;

import java.util.List;

public interface CommunityService {
    CreateCommunityResponseDto createCommunity(CreateCommunityRequestDto createDto);

    JoinCommunityResponseDto joinCommunity(JoinCommunityRequestDto communityRequestDto);

    JoinCommunityResponseDto findCommunity(Long communityId);

    List<CommunityDto> getAllCommunities(Integer page, Integer size);
}
