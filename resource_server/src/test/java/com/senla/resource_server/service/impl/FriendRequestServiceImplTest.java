package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.FriendRequestDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalArgumentException;
import com.senla.resource_server.exception.IllegalStateException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    void shouldAllowToSendFriendRequestAgain_ifPreviousWasDeleted() {
        // given
        String senderEmail = "sender@example.com";
        String recipientEmail = "receiver@example.com";

        User sender = new User();
        sender.setEmail(senderEmail);

        User recipient = new User();
        recipient.setEmail(recipientEmail);

        FriendRequest existingRequest = new FriendRequest();
        existingRequest.setSender(sender);
        existingRequest.setRecipient(recipient);
        existingRequest.setStatus(FriendRequestStatus.DELETED);

        SendFriendRequestDto dto = new SendFriendRequestDto();
        dto.setReceiverEmail(recipientEmail);

        mockAuthentication(sender.getEmail());

        FriendRequest newRequest = new FriendRequest();
        newRequest.setSender(sender);
        newRequest.setRecipient(recipient);
        newRequest.setStatus(FriendRequestStatus.UNDEFINED);

        SendFriendResponseDto responseDto = new SendFriendResponseDto();

        when(userDao.findByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.of(recipient));
        when(friendRequestDao.findBySenderAndRecipient(sender, recipient)).thenReturn(Optional.of(existingRequest));
        when(friendRequestDao.save(any(FriendRequest.class))).thenReturn(newRequest);
        when(friendRequestMapper.toSendFriendResponseDto(newRequest)).thenReturn(responseDto);

        // when
        SendFriendResponseDto result = friendRequestService.sendFriendRequest(dto);

        // then
        assertThat(result).isNotNull();
        verify(friendRequestDao).save(any(FriendRequest.class));
        verify(friendRequestMapper).toSendFriendResponseDto(any(FriendRequest.class));
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
    void shouldThrowExceptionIfAlreadyFriends() {
        // given
        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("receiver@example.com");

        mockAuthentication(sender.getEmail());

        FriendRequest existing = FriendRequest.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.ACCEPTED).build();

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
    void shouldResetDeletedOrRejectedRequestToUndefined() {
        // given
        User sender = new User();
        sender.setEmail("sender@example.com");

        User recipient = new User();
        recipient.setEmail("receiver@example.com");

        mockAuthentication(sender.getEmail());

        FriendRequest existing = FriendRequest.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.REJECTED).build();

        FriendRequest updated = FriendRequest.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.ACCEPTED).build();

        SendFriendResponseDto expectedDto = new SendFriendResponseDto();

        SendFriendRequestDto requestDto = new SendFriendRequestDto();
        requestDto.setReceiverEmail(recipient.getEmail());

        when(userDao.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(userDao.findByEmail(recipient.getEmail())).thenReturn(Optional.of(recipient));
        when(friendRequestDao.findBySenderAndRecipient(sender, recipient)).thenReturn(Optional.ofNullable(existing));
        when(friendRequestDao.save(existing)).thenReturn(updated);
        when(friendRequestMapper.toSendFriendResponseDto(updated)).thenReturn(expectedDto);

        // when
        SendFriendResponseDto actual = friendRequestService.sendFriendRequest(requestDto);

        // then
        assertThat(actual).isEqualTo(expectedDto);
        assertThat(existing.getStatus()).isEqualTo(FriendRequestStatus.UNDEFINED);
    }

    @Test
    void respondToFriendRequest_WhenValid_ShouldProcessSuccessfully() {
        // given
        AnswerFriendRequestDto answerRequestDto = new AnswerFriendRequestDto();
        answerRequestDto.setRequestId(1L);
        answerRequestDto.setStatus(FriendRequestStatus.ACCEPTED);

        String email = "recipient@example.com";

        mockAuthentication(email);

        User recipient = User.builder()
                .email(email)
                .friends(new HashSet<>())
                .build();

        User sender = User.builder()
                .email("sender@example.com")
                .friends(new HashSet<>())
                .build();

        FriendRequest request = new FriendRequest();
        request.setId(1L);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        FriendRequest savedRequest = new FriendRequest();
        savedRequest.setId(1L);
        savedRequest.setSender(sender);
        savedRequest.setRecipient(recipient);
        savedRequest.setStatus(FriendRequestStatus.ACCEPTED);

        UserDto senderDto = new UserDto();
        senderDto.setEmail("sender@example.com");

        AnswerFriendResponseDto expectedResponse = new AnswerFriendResponseDto();
        expectedResponse.setSender(senderDto);
        expectedResponse.setStatus(FriendRequestStatus.ACCEPTED);

        when(friendRequestDao.findById(1L)).thenReturn(Optional.of(request));
        when(userDao.findByEmail(email)).thenReturn(Optional.of(recipient));
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(friendRequestDao.save(any(FriendRequest.class))).thenReturn(savedRequest);
        when(friendRequestMapper.toAnswerFriendResponseDto(savedRequest)).thenReturn(expectedResponse);

        // when
        AnswerFriendResponseDto actualResponse = friendRequestService.respondToFriendRequest(answerRequestDto);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getStatus()).isEqualTo(expectedResponse.getStatus());
        assertThat(actualResponse.getSender().getEmail()).isEqualTo(expectedResponse.getSender().getEmail());
    }

    @Test
    void respondToFriendRequest_WhenStatusRejected_ShouldNotAddFriends() {
        // given
        String recipientEmail = "recipient@example.com";
        String senderEmail = "sender@example.com";

        mockAuthentication(recipientEmail);

        User recipient = User.builder()
                .email(recipientEmail)
                .friends(new HashSet<>())
                .build();

        User sender = User.builder()
                .email(senderEmail)
                .friends(new HashSet<>())
                .build();

        FriendRequest request = new FriendRequest();
        request.setId(1L);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        AnswerFriendRequestDto dto = new AnswerFriendRequestDto();
        dto.setRequestId(1L);
        dto.setStatus(FriendRequestStatus.REJECTED);

        when(friendRequestDao.findById(1L)).thenReturn(Optional.of(request));
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.of(recipient));
        when(friendRequestDao.save(any(FriendRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(friendRequestMapper.toAnswerFriendResponseDto(any(FriendRequest.class)))
                .thenReturn(new AnswerFriendResponseDto());

        // when
        AnswerFriendResponseDto response = friendRequestService.respondToFriendRequest(dto);

        // then
        assertThat(sender.getFriends()).doesNotContain(recipient);
        assertThat(recipient.getFriends()).doesNotContain(sender);
    }

    @Test
    void respondToFriendRequest_WhenUserNotRecipient_ShouldThrowIllegalStateException() {
        // given
        AnswerFriendRequestDto answerRequestDto = new AnswerFriendRequestDto();
        answerRequestDto.setRequestId(1L);
        answerRequestDto.setStatus(FriendRequestStatus.ACCEPTED);

        String authEmail = "not_recipient@example.com";
        String requestRecipientEmail = "recipient@example.com";

        mockAuthentication(requestRecipientEmail);

        User sender = new User();

        User nonFriend = User.builder()
                .email(authEmail)
                .build();

        User recipient = User.builder()
                .email(requestRecipientEmail)
                .build();

        FriendRequest request = new FriendRequest();
        request.setId(1L);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        when(friendRequestDao.findById(1L)).thenReturn(Optional.of(request));
        when(userDao.findByEmail(recipient.getEmail())).thenReturn(Optional.ofNullable(nonFriend));

        // when / then
        assertThatThrownBy(() -> friendRequestService.respondToFriendRequest(answerRequestDto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void respondToFriendRequest_WhenRequestAlreadyProcessed_ShouldThrowIllegalStateException() {
        // given
        AnswerFriendRequestDto answerRequestDto = new AnswerFriendRequestDto();
        answerRequestDto.setRequestId(1L);
        answerRequestDto.setStatus(FriendRequestStatus.ACCEPTED);

        User sender = User.builder()
                .email("sender@example.com")
                .friends(new HashSet<>())
                .build();

        User recipient = User.builder()
                .email("recipient@example.com")
                .friends(new HashSet<>())
                .build();

        mockAuthentication(recipient.getEmail());

        FriendRequest request = new FriendRequest();
        request.setId(1L);
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.ACCEPTED);

        when(friendRequestDao.findById(1L)).thenReturn(Optional.of(request));
        when(userDao.findByEmail(recipient.getEmail())).thenReturn(Optional.of(recipient));

        // when / then
        assertThatThrownBy(() -> friendRequestService.respondToFriendRequest(answerRequestDto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deleteFriend_WhenFriendRequestAccepted_ShouldSuccessfullyDeleteFriend() {
        // given
        DeleteFriendRequest deleteFriendRequest = new DeleteFriendRequest();
        deleteFriendRequest.setFriendEmail("friend@example.com");
        deleteFriendRequest.setRequestId(1L);

        User sender = User.builder()
                .email("sender@example.com")
                .friends(new HashSet<>())
                .build();

        User friend = User.builder()
                .email("friend@example.com")
                .friends(new HashSet<>())
                .build();

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setRecipient(friend);
        request.setStatus(FriendRequestStatus.ACCEPTED);

        mockAuthentication(sender.getEmail());

        when(friendRequestDao.findById(1L)).thenReturn(Optional.of(request));
        when(userDao.findByEmail("sender@example.com")).thenReturn(Optional.of(request.getSender()));
        when(userDao.findByEmail("friend@example.com")).thenReturn(Optional.of(request.getRecipient()));

        // when
        friendRequestService.deleteFriend(deleteFriendRequest);

        // then
        verify(friendRequestDao).save(request);
        assertThat(request.getStatus()).isEqualTo(FriendRequestStatus.DELETED);
    }

    @Test
    void deleteFriend_WhenUserNotParticipant_ShouldThrowIllegalArgumentException() {
        // given
        DeleteFriendRequest deleteFriendRequest = new DeleteFriendRequest();
        deleteFriendRequest.setFriendEmail("recipient@example.com");
        deleteFriendRequest.setRequestId(1L);

        User sender = User.builder()
                .email("sender@example.com")
                .friends(new HashSet<>())
                .build();

        User recipient = User.builder()
                .email("recipient@example.com")
                .friends(new HashSet<>())
                .build();

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.ACCEPTED);

        mockAuthentication("stranger@example.com");

        when(friendRequestDao.findById(1L)).thenReturn(Optional.of(request));

        // when / then
        assertThatThrownBy(() -> friendRequestService.deleteFriend(deleteFriendRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteFriend_WhenFriendRequestNotAccepted_ShouldThrowIllegalArgumentException() {
        // given
        DeleteFriendRequest deleteFriendRequest = new DeleteFriendRequest();
        deleteFriendRequest.setFriendEmail("recipient@example.com");
        deleteFriendRequest.setRequestId(1L);

        User sender = User.builder()
                .email("sender@example.com")
                .friends(new HashSet<>())
                .build();

        User recipient = User.builder()
                .email("recipient@example.com")
                .friends(new HashSet<>())
                .build();

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequestStatus.UNDEFINED);

        mockAuthentication("sender@example.com");

        when(friendRequestDao.findById(1L)).thenReturn(Optional.of(request));

        // when / then
        assertThatThrownBy(() -> friendRequestService.deleteFriend(deleteFriendRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
