package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.FriendRequestDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalArgumentException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.exception.UserNotYourFriendException;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.mapper.FriendRequestMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public final class FriendRequestServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private FriendRequestMapper friendRequestMapper;

    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;

    private void mockAuthentication(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void sendFriendRequest_WhenRequestIsValid_ShouldReturnSendFriendResponseDto() {
        // given
        SendFriendRequestDto requestDto = new SendFriendRequestDto();
        requestDto.setReceiverEmail("receiver@example.com");

        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("receiver@example.com");

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequestStatus.UNDEFINED);

        SendFriendResponseDto responseDto = new SendFriendResponseDto();
        responseDto.setStatus(FriendRequestStatus.UNDEFINED);

        mockAuthentication(sender.getEmail());

        when(userDao.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userDao.findByEmail("receiver@example.com")).thenReturn(Optional.of(recipient));
        when(friendRequestDao.findBySenderAndRecipient(sender, recipient)).thenReturn(Optional.empty());
        when(friendRequestDao.save(any(FriendRequest.class))).thenReturn(friendRequest);
        when(friendRequestMapper.toSendFriendResponseDto(friendRequest)).thenReturn(responseDto);

        // when
        SendFriendResponseDto result = friendRequestService.sendFriendRequest(requestDto);

        // then
        assertThat(result.getStatus()).isEqualTo(FriendRequestStatus.UNDEFINED);
    }

    @Test
    void sendFriendRequest_WhenSenderEqualsReceiver_ShouldThrowIllegalArgumentException() {
        // given
        SendFriendRequestDto requestDto = new SendFriendRequestDto();
        requestDto.setReceiverEmail("sender@example.com");

        mockAuthentication(requestDto.getReceiverEmail());

        // when / then
        assertThatThrownBy(() -> friendRequestService.sendFriendRequest(requestDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sendFriendRequest_WhenSenderNotFound_ShouldThrowEntityNotFoundException() {
        // given
        SendFriendRequestDto requestDto = new SendFriendRequestDto();
        requestDto.setReceiverEmail("receiver@example.com");

        User sender = new User();
        sender.setEmail("sender@example.com");

        mockAuthentication(sender.getEmail());

        when(userDao.findByEmail(sender.getEmail())).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> friendRequestService.sendFriendRequest(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void sendFriendRequest_WhenRecipientNotFound_ShouldThrowEntityNotFoundException() {
        // given
        SendFriendRequestDto requestDto = new SendFriendRequestDto();
        requestDto.setReceiverEmail("receiver@example.com");

        User sender = new User();
        sender.setEmail("sender@example.com");

        mockAuthentication(sender.getEmail());

        when(userDao.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userDao.findByEmail("receiver@example.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> friendRequestService.sendFriendRequest(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void sendFriendRequest_WhenRequestAlreadySent_ShouldThrowIllegalStateException() {
        // given
        SendFriendRequestDto requestDto = new SendFriendRequestDto();
        requestDto.setReceiverEmail("receiver@example.com");

        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("receiver@example.com");

        FriendRequest existingRequest = new FriendRequest();
        existingRequest.setSender(sender);
        existingRequest.setRecipient(recipient);
        existingRequest.setStatus(FriendRequestStatus.UNDEFINED);

        mockAuthentication(sender.getEmail());

        when(userDao.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userDao.findByEmail("receiver@example.com")).thenReturn(Optional.of(recipient));
        when(friendRequestDao.findBySenderAndRecipient(sender, recipient)).thenReturn(Optional.of(existingRequest));

        // when / then
        assertThatThrownBy(() -> friendRequestService.sendFriendRequest(requestDto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldThrowExceptionIfRequestAlreadySent() {
        // given
        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("receiver@example.com");

        mockAuthentication(sender.getEmail());

        FriendRequest existing = FriendRequest.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.UNDEFINED).build();

        SendFriendRequestDto requestDto = new SendFriendRequestDto();
        requestDto.setReceiverEmail(recipient.getEmail());

        when(userDao.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(userDao.findByEmail(recipient.getEmail())).thenReturn(Optional.of(recipient));
        when(friendRequestDao.findBySenderAndRecipient(sender, recipient)).thenReturn(Optional.ofNullable(existing));

        // when + then
        assertThatThrownBy(() -> friendRequestService.sendFriendRequest(requestDto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void sendFriendRequest_shouldAllowNewRequest_whenExistingRequestStatusIsNotUndefined() {
        // given
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipientDto@example.com";

        SendFriendRequestDto requestDto = SendFriendRequestDto.builder()
                .receiverEmail(recipientEmail)
                .build();

        User sender = User.builder()
                .email(senderEmail)
                .build();

        User recipient = User.builder()
                .email(recipientEmail)
                .build();

        UserDto recipientDto = UserDto.builder()
                .email(recipientEmail)
                .build();

        FriendRequest existingRequest = FriendRequest.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.ACCEPTED) // статус не UNDEFINED
                .build();

        FriendRequest savedRequest = FriendRequest.builder()
                .id(1L)
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.UNDEFINED)
                .build();

        SendFriendResponseDto responseDto = SendFriendResponseDto.builder()
                .recipient(recipientDto)
                .build();

        when(userDao.findByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.of(recipient));
        when(friendRequestDao.findBySenderAndRecipient(sender, recipient)).thenReturn(Optional.of(existingRequest));
        when(friendRequestDao.save(any(FriendRequest.class))).thenReturn(savedRequest);
        when(friendRequestMapper.toSendFriendResponseDto(savedRequest)).thenReturn(responseDto);

        // when
        SendFriendResponseDto actualResponse = friendRequestService.sendFriendRequest(requestDto);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getRecipient().getEmail()).isEqualTo(savedRequest.getRecipient().getEmail());
    }

    @Test
    void sendFriendRequest_ShouldThrow_WhenAlreadyFriends() {
        SendFriendRequestDto requestDto = new SendFriendRequestDto("friend@example.com");

        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("friend@example.com");
        recipient.setFriends(Set.of(sender));

        mockAuthentication(sender.getEmail());

        when(userDao.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userDao.findByEmail("friend@example.com")).thenReturn(Optional.of(recipient));

        assertThatThrownBy(() -> friendRequestService.sendFriendRequest(requestDto))
                .isInstanceOf(EntityExistException.class)
                .hasMessage("User already friend");
    }

    @Test
    void respondToFriendRequest_shouldAcceptRequestAndAddFriendWhenStatusAccepted() {
        // Given
        Long requestId = 1L;
        AnswerFriendRequestDto requestDto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.ACCEPTED);

        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("recipient@example.com");

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequestStatus.UNDEFINED);

        mockAuthentication(recipient.getEmail());

        AnswerFriendResponseDto expectedResponse = new AnswerFriendResponseDto();

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(friendRequest));
        when(userDao.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(friendRequestMapper.toAnswerFriendResponseDto(friendRequest)).thenReturn(expectedResponse);

        // When
        AnswerFriendResponseDto response = friendRequestService.respondToFriendRequest(requestDto);

        // Then
        assertThat(response).isEqualTo(expectedResponse);
        assertThat(friendRequest.getStatus()).isEqualTo(FriendRequestStatus.ACCEPTED);
        verify(friendRequestDao).delete(requestId);
    }


    @Test
    void respondToFriendRequest_shouldRejectRequestWhenStatusRejected() {
        // Given
        Long requestId = 1L;
        AnswerFriendRequestDto requestDto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.REJECTED);

        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("recipient@example.com");

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequestStatus.UNDEFINED);

        mockAuthentication(recipient.getEmail());

        AnswerFriendResponseDto expectedResponse = new AnswerFriendResponseDto();

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(friendRequest));
        when(userDao.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(friendRequestMapper.toAnswerFriendResponseDto(friendRequest)).thenReturn(expectedResponse);

        // When
        AnswerFriendResponseDto response = friendRequestService.respondToFriendRequest(requestDto);

        // Then
        assertThat(response).isEqualTo(expectedResponse);
        assertThat(friendRequest.getStatus()).isEqualTo(FriendRequestStatus.REJECTED);
        verify(friendRequestDao).delete(requestId);
        verify(friendRequestMapper).toAnswerFriendResponseDto(friendRequest);
    }


    @Test
    void respondToFriendRequest_shouldThrowExceptionWhenRequestNotFound() {
        // Given
        Long requestId = 1L;
        AnswerFriendRequestDto requestDto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.ACCEPTED);

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> friendRequestService.respondToFriendRequest(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Request not found");
    }

    @Test
    void respondToFriendRequest_shouldThrowExceptionWhenUserNotFound() {
        // Given
        Long requestId = 1L;
        AnswerFriendRequestDto requestDto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.ACCEPTED);

        User sender = User.builder().email("sender@example.com").build();
        User recipient = User.builder().email("recipient@example.com").build();

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);

        mockAuthentication("sender@example.com");

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(friendRequest));
        when(userDao.findByEmail("sender@example.com")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> friendRequestService.respondToFriendRequest(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Not found User");
    }

    @Test
    void respondToFriendRequest_shouldThrowExceptionWhenUserNotAuthorized() {
        // Given
        Long requestId = 1L;
        AnswerFriendRequestDto requestDto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.ACCEPTED);

        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("recipient@example.com");

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);

        mockAuthentication("unauthorized@example.com");

        User unauthorizedUser = new User();
        unauthorizedUser.setEmail("unauthorized@example.com");

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(friendRequest));
        when(userDao.findByEmail("unauthorized@example.com")).thenReturn(Optional.of(unauthorizedUser));

        // When / Then
        assertThatThrownBy(() -> friendRequestService.respondToFriendRequest(requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The request has already been processed or the user is not authorised to process the request");
    }

    @Test
    void respondToFriendRequest_shouldNotDeleteRequestWhenStatusUndefined() {
        // Given
        Long requestId = 1L;
        AnswerFriendRequestDto requestDto = new AnswerFriendRequestDto(requestId, FriendRequestStatus.UNDEFINED);

        User sender = User.builder()
                .email("sender@example.com")
                .build();

        User recipient = User.builder()
                .email("recipient@example.com")
                .build();

        mockAuthentication("recipient@example.com");

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.UNDEFINED)
                .build();

        AnswerFriendResponseDto expectedResponse = new AnswerFriendResponseDto();

        when(friendRequestDao.findById(requestId)).thenReturn(Optional.of(friendRequest));
        when(userDao.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(friendRequestMapper.toAnswerFriendResponseDto(friendRequest)).thenReturn(expectedResponse);

        // When
        AnswerFriendResponseDto response = friendRequestService.respondToFriendRequest(requestDto);

        // Then
        assertThat(response).isEqualTo(expectedResponse);
        assertThat(friendRequest.getStatus()).isEqualTo(FriendRequestStatus.UNDEFINED);
        verify(friendRequestDao, never()).delete(anyLong());
    }


    @Test
    void deleteFriend_ShouldDeleteSuccessfully_WhenUsersAreFriends() {
        // given
        String userEmail = "user@example.com";
        String friendEmail = "friend@example.com";

        User user = User.builder()
                .email(userEmail)
                .build();

        User friend = User.builder()
                .email(friendEmail)
                .build();

        user.setFriends(new HashSet<>(Set.of(friend)));
        friend.setFriends(new HashSet<>(Set.of(user)));

        DeleteFriendRequest request = DeleteFriendRequest.builder()
                .friendEmail(friendEmail)
                .build();

        mockAuthentication(userEmail);

        when(userDao.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userDao.findByEmail(friendEmail)).thenReturn(Optional.of(friend));

        // when
        friendRequestService.deleteFriend(request);

        // then
        assertThat(user.getFriends()).doesNotContain(friend);
        assertThat(friend.getFriends()).doesNotContain(user);

        verify(userDao).save(user);
        verify(userDao).save(friend);
    }


    @Test
    void deleteFriend_ShouldThrow_WhenUserNotFound() {
        // given
        when(userDao.findByEmail("user@example.com")).thenReturn(Optional.empty());

        DeleteFriendRequest request = new DeleteFriendRequest();
        request.setFriendEmail("friend@example.com");

        mockAuthentication("user@example.com");

        // when / then
        assertThatThrownBy(() -> friendRequestService.deleteFriend(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Not found User");
    }

    @Test
    void deleteFriend_ShouldThrow_WhenFriendNotFound() {
        // given
        String userEmail = "user@example.com";
        String friendEmail = "friend@example.com";

        User user = User.builder()
                .email(userEmail)
                .friends(Set.of(User.builder().email(friendEmail).build()))
                .build();
        mockAuthentication(userEmail);

        when(userDao.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userDao.findByEmail(friendEmail)).thenReturn(Optional.empty());

        DeleteFriendRequest request = new DeleteFriendRequest();
        request.setFriendEmail(friendEmail);

        // when / then
        assertThatThrownBy(() -> friendRequestService.deleteFriend(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteFriend_ShouldThrow_WhenFriendIsNotInUserFriendsList() {
        // given
        String userEmail = "user@example.com";
        String friendEmail = "friend@example.com";

        User user = new User();
        user.setEmail(userEmail);
        user.setFriends(new HashSet<>());

        mockAuthentication(user.getEmail());

        when(userDao.findByEmail(userEmail)).thenReturn(Optional.of(user));

        DeleteFriendRequest request = new DeleteFriendRequest();
        request.setFriendEmail(friendEmail);

        // when / then
        assertThatThrownBy(() -> friendRequestService.deleteFriend(request))
                .isInstanceOf(UserNotYourFriendException.class);
    }
}
