package com.senla.resource_server.service.impl;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private User creator;
    private User otherUser;
    private GroupChat groupChat;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setEmail("creator@example.com");

        otherUser = new User();
        otherUser.setEmail("other@example.com");

        groupChat = new GroupChat();
        groupChat.setId(10L);
        groupChat.setName("Test Group");
        groupChat.setUsers(new HashSet<>(List.of(creator, otherUser)));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(creator.getEmail());
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void shouldCreateGroupChatSuccessfully() {
        // given
        CreateGroupChatRequestDto requestDto = new CreateGroupChatRequestDto();
        requestDto.setName("Test Group");
        requestDto.setUserEmails(new HashSet<>(Set.of(otherUser.getEmail())));

        CreateGroupChatResponseDto actual = CreateGroupChatResponseDto.builder()
                .id(1L)
                .name("Test Group")
                .build();

        when(userDao.findByEmail(creator.getEmail())).thenReturn(Optional.of(creator));
        when(userDao.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
        when(groupDao.save(any(GroupChat.class))).thenReturn(groupChat);
        when(groupChatMapper.toCreateGroupDtoResponse(any())).thenReturn(actual);

        // when
        CreateGroupChatResponseDto response = service.create(requestDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(actual.getId());
        assertThat(response.getName()).isEqualTo(actual.getName());
    }

    @Test
    void shouldIgnoreInvalidUserIds_whenCreatingGroupChat() {
        // given
        String invalidUserEmail = "incorect@example.com";
        CreateGroupChatRequestDto requestDto = new CreateGroupChatRequestDto();
        requestDto.setName("Partial Group");
        requestDto.setUserEmails(new HashSet<>(Set.of(otherUser.getEmail(), invalidUserEmail)));

        when(userDao.findByEmail(creator.getEmail())).thenReturn(Optional.of(creator));
        when(userDao.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));
        when(userDao.findByEmail(invalidUserEmail)).thenReturn(Optional.empty());
        when(groupDao.save(any(GroupChat.class))).thenReturn(groupChat);
        when(groupChatMapper.toCreateGroupDtoResponse(any())).thenReturn(new CreateGroupChatResponseDto());

        // when
        CreateGroupChatResponseDto response = service.create(requestDto);

        // then
        assertThat(response).isNotNull();
        verify(userDao).findByEmail(invalidUserEmail);
    }
}

