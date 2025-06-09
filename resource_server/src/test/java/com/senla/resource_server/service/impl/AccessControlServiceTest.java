package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceTest {

    @Mock
    private CommunityDao communityDao;

    @Mock
    private GroupChatDao groupChatDao;

    @InjectMocks
    private AccessControlService accessControlService;

    private User adminUser;
    private User otherUser;
    private Community community;
    private GroupChat groupChat;

    @BeforeEach
    void setUp() {
        adminUser = User.builder().id(1L).email("admin@example.com").build();
        otherUser = User.builder().id(2L).email("other@example.com").build();

        community = Community.builder()
                .id(10L)
                .admin(adminUser)
                .build();

        groupChat = GroupChat.builder()
                .id(20L)
                .name("Group Chat")
                .users(Set.of(adminUser))
                .build();
    }

    @Test
    void verifyUserIsCommunityAdmin_shouldReturnCommunity_whenUserIsAdmin() {
        when(communityDao.findById(community.getId())).thenReturn(Optional.of(community));

        Community result = accessControlService.verifyUserIsCommunityAdmin(community.getId(), adminUser);

        assertThat(result).isEqualTo(community);
        verify(communityDao).findById(community.getId());
    }

    @Test
    void verifyUserIsCommunityAdmin_shouldThrow_whenCommunityNotFound() {
        when(communityDao.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accessControlService.verifyUserIsCommunityAdmin(1L, adminUser))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Community not found");
    }

    @Test
    void verifyUserIsCommunityAdmin_shouldThrow_whenUserIsNotAdmin() {
        when(communityDao.findById(community.getId())).thenReturn(Optional.of(community));

        assertThatThrownBy(() -> accessControlService.verifyUserIsCommunityAdmin(community.getId(), otherUser))
                .isInstanceOf(UserNotAdminInGroupException.class)
                .hasMessage("User is not admin in community");
    }

    @Test
    void verifyUserIsGroupChatMember_shouldReturnGroupChat_whenUserIsMember() {
        when(groupChatDao.findById(groupChat.getId())).thenReturn(Optional.of(groupChat));

        GroupChat result = accessControlService.verifyUserIsGroupChatMember(groupChat.getId(), adminUser);

        assertThat(result).isEqualTo(groupChat);
        verify(groupChatDao).findById(groupChat.getId());
    }

    @Test
    void verifyUserIsGroupChatMember_shouldThrow_whenGroupChatNotFound() {
        when(groupChatDao.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accessControlService.verifyUserIsGroupChatMember(1L, adminUser))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void verifyUserIsGroupChatMember_shouldThrow_whenUserIsNotMember() {
        when(groupChatDao.findById(groupChat.getId())).thenReturn(Optional.of(groupChat));

        assertThatThrownBy(() -> accessControlService.verifyUserIsGroupChatMember(groupChat.getId(), otherUser))
                .isInstanceOf(UserNotInGroupChatException.class);
    }
}

