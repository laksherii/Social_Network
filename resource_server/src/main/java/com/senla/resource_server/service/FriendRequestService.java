package com.senla.resource_server.service;

import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;

public interface FriendRequestService {
    void sendFriendRequest(Long senderId, Long recipientId);

    SendFriendResponseDto sendFriendRequest(SendFriendRequestDto friendRequestDto);

    void respondToFriendRequest(Long requestId, FriendRequestStatus status);

    void deleteFriend(Long requestId, FriendRequestStatus status);
}

