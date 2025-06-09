package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.PublicMessageDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import com.senla.resource_server.service.mapper.MessageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicMessageServiceImplTest {

    @Mock
    private PublicMessageDao publicMessageDao;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private AuthService authService;

    @Mock
    private AccessControlService accessControlService;

    @InjectMocks
    private PublicMessageServiceImpl publicMessageService;

    @Test
    void sendCommunityMessage_shouldReturnResponseDto_whenSuccess() {
        User user = User.builder().email("user@example.com").build();
        Community community = new Community();
        community.setAdmin(user);

        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(1L)
                .message("Hello Community")
                .build();

        PublicMessage savedMessage = PublicMessage.builder()
                .id(10L)
                .community(community)
                .content(requestDto.getMessage())
                .sender(user)
                .build();

        SendCommunityMessageResponseDto responseDto = SendCommunityMessageResponseDto.builder()
                .communityName("Community Name")
                .content(requestDto.getMessage())
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(accessControlService.verifyUserIsCommunityAdmin(requestDto.getCommunityId(), user)).thenReturn(community);
        when(publicMessageDao.save(any(PublicMessage.class))).thenReturn(savedMessage);
        when(messageMapper.toCommunityMessageResponse(savedMessage)).thenReturn(responseDto);

        SendCommunityMessageResponseDto result = publicMessageService.sendCommunityMessage(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getCommunityName()).isEqualTo("Community Name");
        assertThat(result.getContent()).isEqualTo("Hello Community");

        verify(authService).getCurrentUser();
        verify(accessControlService).verifyUserIsCommunityAdmin(1L, user);
        verify(publicMessageDao).save(any(PublicMessage.class));
        verify(messageMapper).toCommunityMessageResponse(savedMessage);
    }

    @Test
    void sendCommunityMessage_shouldThrow_whenUserIsNotAdmin() {
        User user = User.builder().email("user@example.com").build();

        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(1L)
                .message("Hello")
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(accessControlService.verifyUserIsCommunityAdmin(1L, user))
                .thenThrow(new UserNotAdminInGroupException("User is not admin"));

        assertThatThrownBy(() -> publicMessageService.sendCommunityMessage(requestDto))
                .isInstanceOf(UserNotAdminInGroupException.class)
                .hasMessage("User is not admin");

        verify(authService).getCurrentUser();
        verify(accessControlService).verifyUserIsCommunityAdmin(1L, user);
    }

    @Test
    void sendGroupChatMessage_shouldReturnResponseDto_whenSuccess() {
        User user = User.builder().email("user@example.com").build();
        GroupChat groupChat = new GroupChat();

        GroupChatMessageRequestDto requestDto = GroupChatMessageRequestDto.builder()
                .groupId(1L)
                .content("Hello Group")
                .build();

        PublicMessage savedMessage = PublicMessage.builder()
                .id(20L)
                .groupChat(groupChat)
                .content(requestDto.getContent())
                .sender(user)
                .build();

        GroupChatMessageResponseDto responseDto = GroupChatMessageResponseDto.builder()
                .content(requestDto.getContent())
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(accessControlService.verifyUserIsGroupChatMember(1L, user)).thenReturn(groupChat);
        when(publicMessageDao.save(any(PublicMessage.class))).thenReturn(savedMessage);
        when(messageMapper.toGroupChatMessageResponse(savedMessage)).thenReturn(responseDto);

        GroupChatMessageResponseDto result = publicMessageService.sendGroupChatMessage(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello Group");

        verify(authService).getCurrentUser();
        verify(accessControlService).verifyUserIsGroupChatMember(1L, user);
        verify(publicMessageDao).save(any(PublicMessage.class));
        verify(messageMapper).toGroupChatMessageResponse(savedMessage);
    }

    @Test
    void sendWallMessage_shouldReturnResponseDto_whenSuccess() {
        User user = User.builder().email("user@example.com").wall(new Wall()).build();
        Wall wall = user.getWall();

        WallRequestDto requestDto = WallRequestDto.builder()
                .message("Hello Wall")
                .build();

        PublicMessage savedMessage = PublicMessage.builder()
                .id(30L)
                .wall(wall)
                .content(requestDto.getMessage())
                .sender(user)
                .build();

        WallResponseDto responseDto = WallResponseDto.builder()
                .content(requestDto.getMessage())
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(publicMessageDao.save(any(PublicMessage.class))).thenReturn(savedMessage);
        when(messageMapper.toWallMessageResponse(savedMessage)).thenReturn(responseDto);

        WallResponseDto result = publicMessageService.sendWallMessage(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello Wall");
    }
}
