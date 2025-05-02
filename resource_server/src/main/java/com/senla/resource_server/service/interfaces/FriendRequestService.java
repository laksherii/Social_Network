package com.senla.resource_server.service.interfaces;

import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;

public interface FriendRequestService {

    SendFriendResponseDto sendFriendRequest(SendFriendRequestDto friendRequestDto);

    AnswerFriendResponseDto respondToFriendRequest(AnswerFriendRequestDto answerRequestDto);

    void deleteFriend(DeleteFriendRequest friendRequestDto);
}

