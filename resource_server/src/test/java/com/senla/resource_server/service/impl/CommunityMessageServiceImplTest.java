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
import com.senla.resource_server.service.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityMessageServiceImplTest {

    @Mock
    private CommunityDao communityDao;

    @Mock
    private UserDao userDaoImpl;

    @Mock
    private CommunityMessageDao communityMessageDaoImpl;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private CommunityMessageServiceImpl communityMessageService;

    private final Long communityId = 1L;
    private final String email = "admin@example.com";

    private CreateCommunityMessageRequestDto requestDto;
    private User user;
    private Community community;
    private CommunityMessage savedMessage;
    private CreateCommunityMessageResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new CreateCommunityMessageRequestDto();
        requestDto.setCommunityId(communityId);
        requestDto.setMessage("Hello community!");

        user = new User();
        user.setEmail(email);

        community = new Community();
        community.setId(communityId);
        community.setName("Test Community");
        community.setAdmin(user);
        community.setMessages(new ArrayList<>());

        savedMessage = new CommunityMessage();
        savedMessage.setId(10L);
        savedMessage.setMessage(requestDto.getMessage());
        savedMessage.setSender(user);
        savedMessage.setCommunity(community);

        responseDto = new CreateCommunityMessageResponseDto();
        responseDto.setCommunityName(savedMessage.getCommunity().getName());
        responseDto.setMessage(savedMessage.getMessage());
    }

    @Test
    void sendCommunityMessage_successfullySendsMessage() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(communityDao.findById(communityId)).thenReturn(Optional.of(community));
        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.of(user));
        when(communityMessageDaoImpl.save(any())).thenReturn(savedMessage);
        when(messageMapper.toCreateCommunityMessageResponseDto(savedMessage)).thenReturn(responseDto);

        // When
        CreateCommunityMessageResponseDto result = communityMessageService.sendCommunityMessage(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCommunityName()).isEqualTo(savedMessage.getCommunity().getName());
        assertThat(result.getMessage()).isEqualTo(savedMessage.getMessage());

    }

    @Test
    void sendCommunityMessage_shouldThrowIfCommunityNotFound() {
        // Given
        when(communityDao.findById(communityId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> communityMessageService.sendCommunityMessage(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Community not found");
    }

    @Test
    void sendCommunityMessage_shouldThrowIfUserNotFound() {
        // Given
        when(communityDao.findById(communityId)).thenReturn(Optional.of(community));
        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.empty());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When / Then
        assertThatThrownBy(() -> communityMessageService.sendCommunityMessage(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void sendCommunityMessage_shouldThrowIfUserNotAdmin() {
        // Given
        User nonAdmin = new User();
        nonAdmin.setEmail("notadmin@example.com");
        community.setAdmin(nonAdmin);

        when(communityDao.findById(communityId)).thenReturn(Optional.of(community));
        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When / Then
        assertThatThrownBy(() -> communityMessageService.sendCommunityMessage(requestDto))
                .isInstanceOf(UserNotAdminInGroupException.class)
                .hasMessage("Only the community administrator can add entries to the community wall");
    }
}
