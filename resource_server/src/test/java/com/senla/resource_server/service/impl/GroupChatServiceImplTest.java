package com.senla.resource_server.service.impl;

import com.senla.resource_server.config.UserIdAuthenticationToken;
import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.mapper.GroupChatMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupChatServiceImplTest {

    @Mock
    private GroupChatDao groupDao;

    @Mock
    private UserDao userDao;

    @Mock
    private GroupChatMapper groupChatMapper;

    @InjectMocks
    private GroupChatServiceImpl service;

    private final Long creatorId = 1L;
    private final Long otherUserId = 2L;

    private User creator;
    private User otherUser;
    private GroupChat groupChat;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(creatorId);
        creator.setEmail("creator@example.com");

        otherUser = new User();
        otherUser.setId(otherUserId);
        otherUser.setEmail("other@example.com");

        groupChat = new GroupChat();
        groupChat.setId(10L);
        groupChat.setName("Test Group");
        groupChat.setUsers(new HashSet<>(List.of(creator, otherUser)));

        UserIdAuthenticationToken authentication = mock(UserIdAuthenticationToken.class);
        when(authentication.getUserId()).thenReturn(creatorId);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void shouldCreateGroupChatSuccessfully() {
        // given
        com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto requestDto = new CreateGroupChatRequestDto();
        requestDto.setName("Test Group");
        requestDto.setUserIds(new HashSet<>(Set.of(otherUserId)));

        when(userDao.findById(creatorId)).thenReturn(Optional.of(creator));
        when(userDao.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(groupDao.save(any(GroupChat.class))).thenReturn(groupChat);
        when(groupChatMapper.toCreateGroupDtoResponse(any())).thenReturn(new CreateGroupChatResponseDto());

        // when
        CreateGroupChatResponseDto response = service.create(requestDto);

        // then
        assertThat(response).isNotNull();
        verify(userDao).findById(creatorId);
        verify(userDao).findById(otherUserId);
        verify(groupDao).save(any(GroupChat.class));
        verify(groupChatMapper).toCreateGroupDtoResponse(any());
    }

    @Test
    void shouldIgnoreInvalidUserIds_whenCreatingGroupChat() {
        // given
        Long invalidUserId = 99L;
        CreateGroupChatRequestDto requestDto = new CreateGroupChatRequestDto();
        requestDto.setName("Partial Group");
        requestDto.setUserIds(new HashSet<>(Set.of(otherUserId, invalidUserId)));

        when(userDao.findById(creatorId)).thenReturn(Optional.of(creator));
        when(userDao.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(userDao.findById(invalidUserId)).thenReturn(Optional.empty());
        when(groupDao.save(any(GroupChat.class))).thenReturn(groupChat);
        when(groupChatMapper.toCreateGroupDtoResponse(any())).thenReturn(new CreateGroupChatResponseDto());

        // when
        CreateGroupChatResponseDto response = service.create(requestDto);

        // then
        assertThat(response).isNotNull();
        verify(userDao).findById(creatorId);
        verify(userDao).findById(otherUserId);
        verify(userDao).findById(invalidUserId);
        verify(groupDao).save(any(GroupChat.class));
    }
}

