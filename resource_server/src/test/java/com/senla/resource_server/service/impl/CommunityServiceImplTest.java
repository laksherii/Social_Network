package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityRequestDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.mapper.CommunityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityServiceImplTest {

    private final String email = "test@example.com";
    @Mock
    private CommunityDao communityDao;
    @Mock
    private UserDao userDaoImpl;
    @Mock
    private CommunityMapper communityMapper;
    @InjectMocks
    private CommunityServiceImpl communityService;

    private void mockAuthentication(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void createCommunity_WhenUserExists_ShouldReturnCreateCommunityResponseDto() {
        // given
        mockAuthentication(email);

        CreateCommunityRequestDto requestDto = new CreateCommunityRequestDto();
        requestDto.setName("Test Community");

        User user = new User();
        user.setEmail(email);

        Community community = new Community();
        community.setName(requestDto.getName());
        community.setAdmin(user);

        CreateCommunityResponseDto responseDto = new CreateCommunityResponseDto();
        responseDto.setCommunityName("Test Community");

        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.of(user));
        when(communityDao.save(any())).thenReturn(community);
        when(communityMapper.toCreateCommunityResponseDto(community)).thenReturn(responseDto);

        // when
        CreateCommunityResponseDto result = communityService.createCommunity(requestDto);

        // then
        assertThat(result.getCommunityName()).isEqualTo("Test Community");
    }

    @Test
    void createCommunity_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        // given
        mockAuthentication(email);

        CreateCommunityRequestDto requestDto = new CreateCommunityRequestDto();
        requestDto.setName("Test Community");

        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> communityService.createCommunity(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void sendCommunityMessage_shouldReturnResponseDto_whenUserIsAdmin() {
        // given
        String email = "admin@example.com";
        Long communityId = 1L;
        String content = "Hello from admin";

        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(communityId)
                .message(content)
                .build();

        User admin = User.builder()
                .email(email)
                .build();

        Community community = Community.builder()
                .id(communityId)
                .name("Test Community")
                .admin(admin)
                .build();

        PublicMessage message = PublicMessage.builder()
                .id(100L)
                .community(community)
                .content(content)
                .sender(admin)
                .build();

        SendCommunityMessageResponseDto expectedResponse = SendCommunityMessageResponseDto.builder()
                .communityName("Test Community")
                .content(content)
                .build();

        mockAuthentication(email);

        // when
        when(communityDao.findById(communityId)).thenReturn(Optional.of(community));
        when(communityDao.sendMessage(any(PublicMessage.class))).thenReturn(message);
        when(communityMapper.toSendCommunityMessageResponseDto(message)).thenReturn(expectedResponse);

        // then
        SendCommunityMessageResponseDto actual = communityService.sendCommunityMessage(requestDto);

        assertThat(actual).isNotNull();
        assertThat(actual.getCommunityName()).isEqualTo("Test Community");
        assertThat(actual.getContent()).isEqualTo(content);

        verify(communityDao).findById(communityId);
        verify(communityDao).sendMessage(any(PublicMessage.class));
        verify(communityMapper).toSendCommunityMessageResponseDto(message);
    }

    @Test
    void sendCommunityMessage_shouldThrowUserNotAdminInGroupException_whenUserIsNotAdmin() {
        // given
        String email = "user@example.com";
        Long communityId = 1L;

        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(communityId)
                .message("Not allowed")
                .build();

        User actualAdmin = User.builder()
                .email("admin@example.com")
                .build();

        Community community = Community.builder()
                .id(communityId)
                .admin(actualAdmin)
                .build();

        mockAuthentication(email);

        // when
        when(communityDao.findById(communityId)).thenReturn(Optional.of(community));

        // then
        assertThatThrownBy(() -> communityService.sendCommunityMessage(requestDto))
                .isInstanceOf(UserNotAdminInGroupException.class)
                .hasMessage("User is not admin in community");

        verify(communityDao).findById(communityId);
        verifyNoMoreInteractions(communityDao, communityMapper);
    }

    @Test
    void sendCommunityMessage_shouldThrowEntityNotFoundException_whenCommunityDoesNotExist() {
        // given
        Long communityId = 99L;
        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(communityId)
                .message("Hello?")
                .build();

        mockAuthentication(email);

        when(communityDao.findById(communityId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> communityService.sendCommunityMessage(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Community with ID: 99 not found");

        verify(communityDao).findById(communityId);
        verifyNoMoreInteractions(communityDao, communityMapper);
    }

    @Test
    void joinCommunity_WhenUserAndCommunityExistAndUserNotMember_ShouldAddUserAndReturnResponse() {
        // given
        mockAuthentication(email);

        JoinCommunityRequestDto requestDto = new JoinCommunityRequestDto();
        requestDto.setCommunityId(1L);

        User user = new User();
        user.setEmail(email);

        Community community = new Community();
        community.setId(1L);
        community.setName("Java Devs");
        community.setUsers(new HashSet<>());

        JoinCommunityResponseDto responseDto = new JoinCommunityResponseDto();
        responseDto.setCommunityName("Java Devs");

        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.of(user));
        when(communityDao.findById(1L)).thenReturn(Optional.of(community));
        when(communityMapper.toJoinCommunityResponseDto(community)).thenReturn(responseDto);

        // when
        JoinCommunityResponseDto result = communityService.joinCommunity(requestDto);

        // then
        assertThat(community.getUsers()).contains(user);
        assertThat(result.getCommunityName()).isEqualTo("Java Devs");
    }

    @Test
    void joinCommunity_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        // given
        mockAuthentication(email);

        JoinCommunityRequestDto requestDto = new JoinCommunityRequestDto();
        requestDto.setCommunityId(1L);

        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> communityService.joinCommunity(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void joinCommunity_WhenCommunityNotFound_ShouldThrowEntityNotFoundException() {
        // given
        mockAuthentication(email);

        JoinCommunityRequestDto requestDto = new JoinCommunityRequestDto();
        requestDto.setCommunityId(1L);

        User user = new User();
        user.setEmail(email);

        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.of(user));
        when(communityDao.findById(1L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> communityService.joinCommunity(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void joinCommunity_WhenUserAlreadyMember_ShouldThrowEntityExistException() {
        // given
        mockAuthentication(email);

        JoinCommunityRequestDto requestDto = new JoinCommunityRequestDto();
        requestDto.setCommunityId(1L);

        User user = new User();
        user.setEmail(email);

        Community community = new Community();
        community.setUsers(new HashSet<>(List.of(user)));

        when(userDaoImpl.findByEmail(email)).thenReturn(Optional.of(user));
        when(communityDao.findById(1L)).thenReturn(Optional.of(community));

        // when / then
        assertThatThrownBy(() -> communityService.joinCommunity(requestDto))
                .isInstanceOf(EntityExistException.class);
    }

    @Test
    void findCommunity_WhenCommunityExists_ShouldReturnJoinCommunityResponseDto() {
        Community community = new Community();
        community.setId(1L);
        community.setName("Spring Devs");

        JoinCommunityResponseDto responseDto = new JoinCommunityResponseDto();
        responseDto.setCommunityName("Spring Devs");

        when(communityDao.findById(1L)).thenReturn(Optional.of(community));
        when(communityMapper.toJoinCommunityResponseDto(community)).thenReturn(responseDto);

        JoinCommunityResponseDto result = communityService.findCommunity(1L);

        assertThat(result.getCommunityName()).isEqualTo("Spring Devs");
    }

    @Test
    void findCommunity_WhenCommunityNotFound_ShouldThrowEntityNotFoundException() {
        when(communityDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> communityService.findCommunity(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Community not found");
    }

    @Test
    void getAllCommunities_WhenNoCommunitiesExist_ShouldReturnEmptyList() {
        when(communityDao.findAll(0, 10)).thenReturn(Collections.emptyList());

        List<CommunityDto> result = communityService.getAllCommunities(0, 10);

        assertThat(result).isEmpty();
    }
}


