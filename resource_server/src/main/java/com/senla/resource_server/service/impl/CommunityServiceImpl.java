package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityRequestDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.interfaces.CommunityService;
import com.senla.resource_server.service.mapper.CommunityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityDao communityDao;
    private final UserDao userDaoImpl;
    private final CommunityMapper communityMapper;

    @Override
    public CreateCommunityResponseDto createCommunity(CreateCommunityRequestDto createDto) {
        log.info("Starting community creation with name: {}", createDto.getName());

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Authenticated user: {}", email);

        User user = userDaoImpl.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("User entity retrieved: {}", user.getEmail());

        Community community = new Community();
        community.setName(createDto.getName());
        community.setDescription(createDto.getDescription());
        community.setAdmin(user);
        log.info("Community entity prepared with name: {} and admin: {}", community.getName(), user.getEmail());

        Community savedCommunity = communityDao.save(community);
        log.info("Successfully created community: {} (ID: {}) with admin: {}",
                community.getName(), community.getId(), user.getEmail());

        return communityMapper.toCreateCommunityResponseDto(savedCommunity);
    }

    @Override
    public JoinCommunityResponseDto joinCommunity(JoinCommunityRequestDto communityRequestDto) {
        log.info("Processing join request for community ID: {}", communityRequestDto.getCommunityId());

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Authenticated user: {}", email);

        User user = userDaoImpl.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("User entity retrieved: {}", user.getEmail());

        Community community = communityDao.findById(communityRequestDto.getCommunityId())
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));
        log.info("Community found: {} (ID: {})", community.getName(), community.getId());

        if (community.getUsers().contains(user)) {
            log.info("User {} already joined community {} (ID: {})", user.getEmail(), community.getName(), community.getId());
            throw new EntityExistException("User already joined to this community");
        }

        community.getUsers().add(user);
        log.info("User {} successfully joined community {} (ID: {})",
                user.getEmail(), community.getName(), community.getId());

        return communityMapper.toJoinCommunityResponseDto(community);
    }

    @Override
    public SendCommunityMessageResponseDto sendCommunityMessage(SendCommunityMessageRequestDto requestDto) {
        log.info("Starting to process community message send request");

        log.info("Retrieving community ID from request: {}", requestDto.getCommunityId());

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Authenticated user: {}", email);

        log.info("Looking up community with ID: {}", requestDto.getCommunityId());
        Community community = communityDao.findById(requestDto.getCommunityId())
                .orElseThrow(() -> new EntityNotFoundException("Community with ID: " + requestDto.getCommunityId() + " not found"));
        log.info("Community found: {} (ID: {})", community.getName(), community.getId());

        log.info("Verifying if authenticated user is the admin of the community");
        if (!community.getAdmin().getEmail().equals(email)) {
            log.info("User {} is not the admin of community ID: {}", email, community.getId());
            throw new UserNotAdminInGroupException("User is not admin in community");
        }
        log.info("User is confirmed as the admin of the community");

        log.info("Building public message for the community");
        PublicMessage communityMessage = PublicMessage.builder()
                .community(community)
                .content(requestDto.getMessage())
                .sender(community.getAdmin())
                .build();
        log.info("Public message entity built successfully");

        log.info("Sending public message to the database");
        PublicMessage message = communityDao.sendMessage(communityMessage);
        log.info("Public message sent successfully with ID: {}", message.getId());

        log.info("Mapping public message to response DTO");
        SendCommunityMessageResponseDto responseDto = communityMapper.toSendCommunityMessageResponseDto(message);
        log.info("Message mapped to response DTO successfully");

        return responseDto;
    }


    @Override
    public JoinCommunityResponseDto findCommunity(Long communityId) {
        log.info("Fetching community by ID: {}", communityId);

        Community community = communityDao.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));
        log.info("Found community: {} (ID: {})", community.getName(), community.getId());

        return communityMapper.toJoinCommunityResponseDto(community);
    }

    @Override
    public List<CommunityDto> getAllCommunities(Integer page, Integer size) {
        log.info("Fetching all communities, page: {}, size: {}", page, size);

        List<Community> allCommunities = communityDao.findAll(page, size);
        log.info("Retrieved {} communities", allCommunities.size());

        return allCommunities.stream()
                .map(communityMapper::toCommunityDto)
                .toList();
    }
}