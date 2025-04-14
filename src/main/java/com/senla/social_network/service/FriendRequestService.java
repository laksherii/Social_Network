package com.senla.social_network.service;

import com.senla.social_network.data.entity.FriendRequest.FriendRequestStatus;

public interface FriendRequestService {
    void sendFriendRequest(Long senderId, Long recipientId);

    void respondToFriendRequest(Long requestId, FriendRequestStatus status);

    void deleteFriend(Long requestId, FriendRequestStatus status);
}

