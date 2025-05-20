package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FriendValidationServiceTest {

    private final FriendValidationService validationService = new FriendValidationService();

    private final User sender = User.builder().build();
    private final User recipient = User.builder().build();

    @Test
    void validateNotSelf_WhenSameEmail_ShouldThrow() {
        // given
        sender.setEmail("test@example.com");
        recipient.setEmail("test@example.com");

        // when / then
        assertThatThrownBy(() -> validationService.validateNotSelf(sender, recipient))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You cannot send a request to yourself");
    }

    @Test
    void validateNotSelf_WhenDifferentEmail_ShouldPass() {
        // given
        sender.setEmail("sender@example.com");
        recipient.setEmail("recipient@example.com");

        // when / then
        assertThatCode(() -> validationService.validateNotSelf(sender, recipient))
                .doesNotThrowAnyException();
    }

    @Test
    void validateNotAlreadyFriends_WhenAlreadyFriends_ShouldThrow() {
        // given
        sender.setEmail("sender@example.com");
        recipient.setEmail("recipient@example.com");

        User friend = new User();
        friend.setEmail("sender@example.com");
        recipient.setFriends(Set.of(friend));

        // when / then
        assertThatThrownBy(() -> validationService.validateNotAlreadyFriends(sender, recipient))
                .isInstanceOf(EntityExistException.class);
    }

    @Test
    void validateNotAlreadyFriends_WhenNotFriends_ShouldPass() {
        // given
        sender.setEmail("sender@example.com");
        recipient.setEmail("recipient@example.com");
        recipient.setFriends(Set.of());

        // when / then
        assertThatCode(() -> validationService.validateNotAlreadyFriends(sender, recipient))
                .doesNotThrowAnyException();
    }

    @Test
    void validateRequestNotSent_WhenRequestIsNull_ShouldPass() {
        // when / then
        assertThatCode(() -> validationService.validateRequestNotSent(null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateRequestNotSent_WhenRequestStatusIsUndefined_ShouldThrow() {
        // given
        FriendRequest request = new FriendRequest();
        request.setStatus(FriendRequestStatus.UNDEFINED);

        // when / then
        assertThatThrownBy(() -> validationService.validateRequestNotSent(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Friend request has already been sent");
    }

    @Test
    void validateRequestNotSent_WhenRequestStatusIsNotUndefined_ShouldPass() {
        // given
        FriendRequest request = new FriendRequest();
        request.setStatus(FriendRequestStatus.REJECTED);

        // when / then
        assertThatCode(() -> validationService.validateRequestNotSent(request))
                .doesNotThrowAnyException();
    }
}

