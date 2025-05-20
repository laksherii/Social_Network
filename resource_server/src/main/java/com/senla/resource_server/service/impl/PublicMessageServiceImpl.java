package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.PublicMessageDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import com.senla.resource_server.service.interfaces.PublicMessageService;
import com.senla.resource_server.service.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PublicMessageServiceImpl implements PublicMessageService {

    private final PublicMessageDao publicMessageDao;
    private final MessageMapper messageMapper;
    private final AuthService authService;
    private final AccessControlService accessControlService;

    @Override
    public SendCommunityMessageResponseDto sendCommunityMessage(SendCommunityMessageRequestDto requestDto) {
        User user = authService.getCurrentUser();
        Community community = accessControlService.verifyUserIsCommunityAdmin(requestDto.getCommunityId(), user);

        PublicMessage message = buildMessageForCommunity(requestDto, community, user);
        PublicMessage savedMessage = publicMessageDao.save(message);

        log.info("Successfully sent community message by user: {}, messageId: {}", user.getEmail(), savedMessage.getId());
        return messageMapper.toCommunityMessageResponse(savedMessage);
    }

    @Override
    public GroupChatMessageResponseDto sendGroupChatMessage(GroupChatMessageRequestDto requestDto) {
        User user = authService.getCurrentUser();
        GroupChat groupChat = accessControlService.verifyUserIsGroupChatMember(requestDto.getGroupId(), user);

        PublicMessage message = buildMessageForGroupChat(requestDto, groupChat, user);
        PublicMessage savedMessage = publicMessageDao.save(message);

        log.info("Successfully sent group chat message by user: {}, messageId: {}", user.getEmail(), savedMessage.getId());
        return messageMapper.toGroupChatMessageResponse(savedMessage);
    }

    @Override
    public WallResponseDto sendWallMessage(WallRequestDto requestDto) {
        User user = authService.getCurrentUser();
        Wall wall = user.getWall();

        PublicMessage message = buildMessageForWall(requestDto, wall, user);
        PublicMessage savedMessage = publicMessageDao.save(message);

        log.info("Successfully sent wall message by user: {}, messageId: {}", user.getEmail(), savedMessage.getId());
        return messageMapper.toWallMessageResponse(savedMessage);
    }

    private PublicMessage buildMessageForCommunity(SendCommunityMessageRequestDto dto, Community community, User sender) {
        return PublicMessage.builder()
                .community(community)
                .content(dto.getMessage())
                .sender(sender)
                .build();
    }

    private PublicMessage buildMessageForGroupChat(GroupChatMessageRequestDto dto, GroupChat groupChat, User sender) {
        return PublicMessage.builder()
                .groupChat(groupChat)
                .content(dto.getContent())
                .sender(sender)
                .build();
    }

    private PublicMessage buildMessageForWall(WallRequestDto dto, Wall wall, User sender) {
        return PublicMessage.builder()
                .wall(wall)
                .content(dto.getMessage())
                .sender(sender)
                .build();
    }
}
