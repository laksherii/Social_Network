package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendValidationService {
    public void validateNotSelf(User sender, User recipient) {
        if (sender.getEmail().equals(recipient.getEmail())) {
            throw new IllegalStateException("You cannot send a request to yourself");
        }
    }

    public void validateNotAlreadyFriends(User sender, User recipient) {
        boolean alreadyFriend = recipient.getFriends().stream()
                .anyMatch(friend -> friend.getEmail().equals(sender.getEmail()));
        if (alreadyFriend) {
            throw new EntityExistException("User already friend");
        }
    }

    public void validateRequestNotSent(FriendRequest existingRequest) {
        if (existingRequest != null && existingRequest.getStatus() == FriendRequestStatus.UNDEFINED) {
            throw new IllegalStateException("Friend request has already been sent");
        }
    }
}

