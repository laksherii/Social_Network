package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityRequestDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityServiceImplTest {

    private final String email = "test@example.com";

    @Mock
    private CommunityDao communityDao;

    @Mock
    private UserDao userDaoImpl;

    @Mock
    private AuthService authService;

    @Mock
    private CommunityMapper communityMapper;

    @InjectMocks
    private CommunityServiceImpl communityService;

    @Test
    void createCommunity_WhenUserExists_ShouldReturnCreateCommunityResponseDto() {

        CreateCommunityRequestDto requestDto = CreateCommunityRequestDto.builder()
                .name("Test Community")
                .build();

        User user = User.builder()
                .email(email)
                .build();

        Community community = Community.builder()
                .name("Test Community")
                .admin(user)
                .build();

        CreateCommunityResponseDto responseDto = CreateCommunityResponseDto.builder()
                .communityName("Test Community")
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(communityDao.save(any())).thenReturn(community);
        when(communityMapper.toCreateCommunityResponseDto(community)).thenReturn(responseDto);

        CreateCommunityResponseDto result = communityService.createCommunity(requestDto);

        assertThat(result.getCommunityName()).isEqualTo("Test Community");
    }


    @Test
    void joinCommunity_WhenUserAndCommunityExistAndUserNotMember_ShouldAddUserAndReturnResponse() {

        JoinCommunityRequestDto requestDto = JoinCommunityRequestDto.builder()
                .communityId(1L)
                .build();

        User user = User.builder()
                .email(email)
                .build();

        Community community = Community.builder()
                .id(1L)
                .name("Java Devs")
                .users(new HashSet<>())
                .build();

        JoinCommunityResponseDto responseDto = JoinCommunityResponseDto.builder()
                .communityName("Java Devs")
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(communityDao.findById(1L)).thenReturn(Optional.of(community));
        when(communityMapper.toJoinCommunityResponseDto(community)).thenReturn(responseDto);

        JoinCommunityResponseDto result = communityService.joinCommunity(requestDto);

        assertThat(community.getUsers()).contains(user);
        assertThat(result.getCommunityName()).isEqualTo("Java Devs");
    }

    @Test
    void joinCommunity_WhenCommunityNotFound_ShouldThrowEntityNotFoundException() {
        JoinCommunityRequestDto requestDto = JoinCommunityRequestDto.builder()
                .communityId(1L)
                .build();

        User user = User.builder()
                .email(email)
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(communityDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> communityService.joinCommunity(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void joinCommunity_WhenUserAlreadyMember_ShouldThrowEntityExistException() {
        JoinCommunityRequestDto requestDto = JoinCommunityRequestDto.builder()
                .communityId(1L)
                .build();

        User user = User.builder()
                .email(email)
                .build();

        Community community = Community.builder()
                .users(new HashSet<>(List.of(user)))
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(communityDao.findById(1L)).thenReturn(Optional.of(community));

        assertThatThrownBy(() -> communityService.joinCommunity(requestDto))
                .isInstanceOf(EntityExistException.class);
    }

    @Test
    void findCommunity_WhenCommunityExists_ShouldReturnJoinCommunityResponseDto() {
        Community community = Community.builder()
                .id(1L)
                .name("Spring Devs")
                .build();

        JoinCommunityResponseDto responseDto = JoinCommunityResponseDto.builder()
                .communityName("Spring Devs")
                .build();

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


