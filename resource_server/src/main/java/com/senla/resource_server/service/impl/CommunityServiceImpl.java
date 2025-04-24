package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.impl.CommunityDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.mapper.CommunityMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityServiceImpl {

    private final CommunityDaoImpl communityDao;
    private final UserDaoImpl userDaoImpl;
    private final CommunityMapper communityMapper;

    public CreateCommunityResponseDto createCommunity(CreateCommunityRequestDto createDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDaoImpl.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Community community = new Community();
        community.setName(createDto.getName());
        community.setAdmin(user);

        communityDao.save(community);

        return communityMapper.toCreateCommunityResponseDto(community);
    }

//    public CreateCommunityResponseDto joinCommunity(CreateCommunityRequestDto createDto) {
//        return communityMapper.toCreateCommunityResponseDto
//    }
}
