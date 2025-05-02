package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.dao.CommunityMessageDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.CommunityMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageResponseDto;
import com.senla.resource_server.service.interfaces.CommunityMessageService;
import com.senla.resource_server.service.mapper.MessageMapper;
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
public class CommunityMessageServiceImpl implements CommunityMessageService {

    private final CommunityDao communityDao;
    private final UserDao userDaoImpl;
    private final CommunityMessageDao communityMessageDaoImpl;
    private final MessageMapper messageMapper;

    @Override
    public CreateCommunityMessageResponseDto sendCommunityMessage(CreateCommunityMessageRequestDto createCommunityMessageDto) {
        log.info("Received request to send a community message: {}", createCommunityMessageDto);

        Long communityId = createCommunityMessageDto.getCommunityId();
        Community community = communityDao.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));
        log.info("Community found: {} (ID: {})", community.getName(), communityId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Authenticated user: {}", email);

        User user = userDaoImpl.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("User entity retrieved: {}", user.getEmail());

        if (!user.getEmail().equals(community.getAdmin().getEmail())) {
            throw new UserNotAdminInGroupException("Only the community administrator can add entries to the community wall");
        }
        log.info("User {} is verified as admin for community {}", user.getEmail(), communityId);

        String message = createCommunityMessageDto.getMessage();
        CommunityMessage communityMessage = new CommunityMessage();
        communityMessage.setCommunity(community);
        communityMessage.setMessage(message);
        communityMessage.setSender(user);
        log.info("CommunityMessage entity created for community {} by user {}", communityId, user.getEmail());

        List<CommunityMessage> messages = community.getMessages();
        messages.add(communityMessage);
        log.info("CommunityMessage added to community message list (communityId: {})", communityId);

        CommunityMessage save = communityMessageDaoImpl.save(communityMessage);
        log.info("Community message saved successfully by user {} to community {}", user.getEmail(), communityId);

        return messageMapper.toCreateCommunityMessageResponseDto(save);
    }
}
