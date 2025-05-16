package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.mapper.GroupChatMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupChatServiceImplTest {

    @InjectMocks
    private GroupChatServiceImpl groupChatService;

    @Mock
    private GroupChatDao groupDao;

    @Mock
    private UserDao userDao;

    @Mock
    private GroupChatMapper groupChatMapper;

    private void mockSecurityContext(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void create_shouldReturnResponseDto_whenGroupChatIsCreatedSuccessfully() {
        // given
        CreateGroupChatRequestDto requestDto = CreateGroupChatRequestDto.builder()
                .name("Test Group")
                .userEmails(new HashSet<>(Set.of("user1@example.com")))
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .build();

        User creator = User.builder()
                .id(2L)
                .email("user@example.com")
                .build();

        GroupChat groupChat = GroupChat.builder()
                .id(1L)
                .name("Test Group")
                .users(Set.of(user1, creator))
                .build();

        CreateGroupChatResponseDto response = CreateGroupChatResponseDto.builder()
                .id(1L)
                .name("Test Group")
                .users(Set.of(UserDto.builder().email("user@example.com").build(),
                        UserDto.builder().email("user1@example.com").build()))
                .build();

        mockSecurityContext(user1.getEmail());

        when(userDao.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));
        when(groupDao.save(any(GroupChat.class))).thenReturn(groupChat);
        when(groupChatMapper.toCreateGroupDtoResponse(any(GroupChat.class))).thenReturn(response);

        // when
        CreateGroupChatResponseDto result = groupChatService.create(requestDto);

        // then
        assertThat(result.getId()).isEqualTo(response.getId());
        assertThat(result.getName()).isEqualTo(response.getName());
        assertThat(result.getUsers()).hasSize(response.getUsers().size());
    }

    @Test
    void sendGroupChatMessage_shouldReturnResponseDto_whenSenderIsInGroupChat() {
        // given
        GroupChatMessageRequestDto requestDto = GroupChatMessageRequestDto.builder()
                .groupId(1L)
                .message("Hello Group!")
                .build();

        User sender = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        GroupChat groupChat = GroupChat.builder()
                .id(1L)
                .users(Set.of(sender))
                .build();

        PublicMessage message = PublicMessage.builder()
                .id(1L)
                .sender(sender)
                .groupChat(groupChat)
                .content("Hello Group!")
                .build();

        GroupChatMessageResponseDto responseDto = GroupChatMessageResponseDto.builder()
                .name("Group Name")
                .content("Hello Group!")
                .build();

        mockSecurityContext(sender.getEmail());

        when(groupDao.findById(1L)).thenReturn(Optional.of(groupChat));
        when(userDao.findByEmail("user@example.com")).thenReturn(Optional.of(sender));
        when(groupDao.sendMessage(any(PublicMessage.class))).thenReturn(message);
        when(groupChatMapper.toGroupChatMessageResponseDto(any(PublicMessage.class))).thenReturn(responseDto);

        // when
        GroupChatMessageResponseDto result = groupChatService.sendGroupChatMessage(requestDto);

        // then
        assertThat(result.getName()).isEqualTo("Group Name");
        assertThat(result.getContent()).isEqualTo("Hello Group!");
    }

    @Test
    void sendGroupChatMessage_shouldThrowException_whenSenderIsNotInGroupChat() {
        // given
        GroupChatMessageRequestDto requestDto = GroupChatMessageRequestDto.builder()
                .groupId(1L)
                .message("Hello Group!")
                .build();

        GroupChat groupChat = GroupChat.builder()
                .id(1L)
                .users(Set.of()) // sender not in group
                .build();

        mockSecurityContext("user@example.com");

        when(groupDao.findById(1L)).thenReturn(Optional.of(groupChat));

        // when + then
        assertThatThrownBy(() -> groupChatService.sendGroupChatMessage(requestDto))
                .isInstanceOf(UserNotInGroupChatException.class);
    }

    @Test
    void findById_shouldReturnGroupChat_whenGroupExists() {
        // given
        GroupChat groupChat = GroupChat.builder()
                .id(1L)
                .name("Test")
                .build();

        GroupChatDto response = GroupChatDto.builder()
                .name("Test")
                .build();

        when(groupDao.findById(1L)).thenReturn(Optional.of(groupChat));
        when(groupChatMapper.toGroupChatDto(groupChat)).thenReturn(response);

        // when
        GroupChatDto actual = groupChatService.findById(1L);

        // then
        assertThat(actual.getName()).isEqualTo(response.getName());
    }

    @Test
    void findById_shouldThrowException_whenGroupDoesNotExist() {
        // given
        when(groupDao.findById(1L)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> groupChatService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
