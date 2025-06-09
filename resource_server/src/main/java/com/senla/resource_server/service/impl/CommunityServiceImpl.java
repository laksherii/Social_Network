package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityRequestDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
import com.senla.resource_server.service.interfaces.CommunityService;
import com.senla.resource_server.service.mapper.CommunityMapper;
import org.springframework.transaction.annotation.Transactional;
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
    private final AuthService authService;

    @Override
    public CreateCommunityResponseDto createCommunity(CreateCommunityRequestDto createDto) {

        User user = authService.getCurrentUser();

        Community community = new Community();
        community.setName(createDto.getName());
        community.setDescription(createDto.getDescription());
        community.setAdmin(user);

        Community savedCommunity = communityDao.save(community);

        log.info("Successfully created community: {} (ID: {}) with admin: {}",
                community.getName(), community.getId(), user.getEmail());

        return communityMapper.toCreateCommunityResponseDto(savedCommunity);
    }

    @Override
    public JoinCommunityResponseDto joinCommunity(JoinCommunityRequestDto communityRequestDto) {

        User user = authService.getCurrentUser();

        Community community = communityDao.findById(communityRequestDto.getCommunityId())
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        if (community.getUsers().contains(user)) {
            throw new EntityExistException("User already joined to this community");
        }

        community.getUsers().add(user);
        log.info("User {} successfully joined community {} (ID: {})",
                user.getEmail(), community.getName(), community.getId());

        return communityMapper.toJoinCommunityResponseDto(community);
    }

    @Override
    public JoinCommunityResponseDto findCommunity(Long communityId) {

        Community community = communityDao.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));
        log.info("Found community: {} (ID: {})", community.getName(), community.getId());

        return communityMapper.toJoinCommunityResponseDto(community);
    }

    @Override
    public List<CommunityDto> getAllCommunities(Integer page, Integer size) {

        List<Community> allCommunities = communityDao.findAll(page, size);
        log.info("Retrieved {} communities", allCommunities.size());

        return allCommunities.stream()
                .map(communityMapper::toCommunityDto)
                .toList();
    }
}