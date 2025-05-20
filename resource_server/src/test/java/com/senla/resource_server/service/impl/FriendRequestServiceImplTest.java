package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.FriendRequestDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.exception.UserNotYourFriendException;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.mapper.FriendRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceImplTest {

    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private UserDao userDao;

    @Mock
    private AuthService authService;

    @Mock
    private FriendRequestMapper friendRequestMapper;

    @Mock
    private FriendValidationService friendValidationService;

    private User sender;
    private User recipient;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");

        recipient = new User();
        recipient.setId(2L);
        recipient.setEmail("recipient@example.com");
    }

    @Test
    void sendFriendRequest_WhenRequestIsValid_ShouldReturnSendFriendResponseDto() {
        // given
        SendFriendRequestDto dto = new SendFriendRequestDto("recipient@example.com");
        SendFriendResponseDto expectedResponse = SendFriendResponseDto.builder()
                .recipient(UserDto.builder().email("recipient@example.com").build())
                .status(FriendRequestStatus.UNDEFINED)
                .build();

        when(authService.getCurrentUser()).thenReturn(sender);
        when(userDao.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(friendRequestDao.findBySenderAndRecipient(sender, recipient)).thenReturn(Optional.empty());
        when(friendRequestDao.save(any(FriendRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(friendRequestMapper.toSendFriendResponseDto(any(FriendRequest.class))).thenReturn(expectedResponse);

        // when
        SendFriendResponseDto actualResponse = friendRequestService.sendFriendRequest(dto);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        assertThat(actualResponse.getStatus()).isEqualTo(expectedResponse.getStatus());

    }

    @Test
    void respondToFriendRequest_WhenAccepted_ShouldAddEachOtherAsFriends() {
        // given
        Long requestId = 1L;
        FriendRequest request = new FriendRequest();
        request.setId(requestId);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        AnswerFriendRequestDto dto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.ACCEPTED);
        AnswerFriendResponseDto expectedResponse = new AnswerFriendResponseDto(
                UserDto.builder().email(sender.getEmail()).build(),
                FriendRequestStatus.ACCEPTED
        );

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(request));
        when(authService.getCurrentUser()).thenReturn(recipient);
        when(friendRequestMapper.toAnswerFriendResponseDto(request)).thenReturn(expectedResponse);

        // when
        AnswerFriendResponseDto actualResponse = friendRequestService.respondToFriendRequest(dto);

        // then
        assertThat(sender.getFriends()).contains(recipient);
        assertThat(recipient.getFriends()).contains(sender);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void respondToFriendRequest_WhenRejected_ShouldNotAddAsFriends() {
        // given
        Long requestId = 2L;
        FriendRequest request = new FriendRequest();
        request.setId(requestId);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        AnswerFriendRequestDto dto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.REJECTED);
        AnswerFriendResponseDto expectedResponse = new AnswerFriendResponseDto(
                UserDto.builder().email(sender.getEmail()).build(),
                FriendRequestStatus.REJECTED
        );

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(request));
        when(authService.getCurrentUser()).thenReturn(recipient);
        when(friendRequestMapper.toAnswerFriendResponseDto(request)).thenReturn(expectedResponse);

        // when
        AnswerFriendResponseDto actualResponse = friendRequestService.respondToFriendRequest(dto);

        // then
        assertThat(sender.getFriends()).doesNotContain(recipient);
        assertThat(recipient.getFriends()).doesNotContain(sender);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void respondToFriendRequest_WhenUserIsNotRecipient_ShouldThrowIllegalStateException() {
        // given
        Long requestId = 3L;
        FriendRequest request = new FriendRequest();
        request.setId(requestId);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setEmail("not_recipient@example.com");

        AnswerFriendRequestDto dto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.ACCEPTED);

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(request));
        when(authService.getCurrentUser()).thenReturn(anotherUser);

        // when / then
        assertThatThrownBy(() -> friendRequestService.respondToFriendRequest(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void respondToFriendRequest_WhenStatusIsUndefined_ShouldNotDeleteOrAddFriend() {
        // given
        Long requestId = 4L;
        FriendRequest request = new FriendRequest();
        request.setId(requestId);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        AnswerFriendRequestDto dto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.UNDEFINED);

        AnswerFriendResponseDto expectedResponse = AnswerFriendResponseDto.builder()
                .sender(UserDto.builder()
                        .email(sender.getEmail())
                        .build())
                .status(FriendRequestStatus.UNDEFINED)
                .build();

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(request));
        when(authService.getCurrentUser()).thenReturn(recipient);
        when(friendRequestMapper.toAnswerFriendResponseDto(request)).thenReturn(expectedResponse);

        // when
        AnswerFriendResponseDto responseDto = friendRequestService.respondToFriendRequest(dto);

        // then
        assertThat(sender.getFriends()).doesNotContain(recipient);
        assertThat(recipient.getFriends()).doesNotContain(sender);
        assertThat(responseDto.getStatus()).isEqualTo(FriendRequestStatus.UNDEFINED);
        assertThat(responseDto.getSender().getEmail()).isEqualTo(sender.getEmail());
    }

    @Test
    void deleteFriend_WhenUserIsFriend_ShouldRemoveFriendSuccessfully() {
        // given

        sender.setFriends(new HashSet<>(Set.of(recipient)));
        recipient.setFriends(new HashSet<>(Set.of(sender)));

        DeleteFriendRequest request = DeleteFriendRequest.builder()
                .friendEmail(recipient.getEmail())
                .build();

        when(authService.getCurrentUser()).thenReturn(sender);
        when(userDao.findByEmail(recipient.getEmail())).thenReturn(Optional.of(recipient));

        // when
        friendRequestService.deleteFriend(request);

        // then
        assertThat(sender.getFriends()).doesNotContain(recipient);
        assertThat(recipient.getFriends()).doesNotContain(sender);

        verify(userDao).save(sender);
        verify(userDao).save(recipient);
    }

    @Test
    void deleteFriend_WhenUserIsNotFriend_ShouldThrowException() {
        // given
        String friendEmail = "stranger@example.com";
        User user = new User();
        user.setEmail("user@example.com");

        DeleteFriendRequest request = new DeleteFriendRequest(friendEmail);

        when(authService.getCurrentUser()).thenReturn(user);

        // when / then
        assertThatThrownBy(() -> friendRequestService.deleteFriend(request))
                .isInstanceOf(UserNotYourFriendException.class)
                .hasMessageContaining(friendEmail);

        verify(userDao, never()).save(any());
    }

}
