package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import com.senla.resource_server.service.impl.FriendRequestServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/friend-request")
public class RestFriendRequestController {

    private final FriendRequestServiceImpl friendRequestService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SendFriendResponseDto sendFriendRequest(@Valid @RequestBody SendFriendRequestDto friendRequestDto) {
        log.info("User send friend request to user with Email {}", friendRequestDto.getReceiverEmail());
        SendFriendResponseDto response = friendRequestService.sendFriendRequest(friendRequestDto);
        log.info("Successfully sent friend request to user with Email {}", friendRequestDto.getReceiverEmail());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public AnswerFriendResponseDto responseFriendRequest(@Valid @RequestBody AnswerFriendRequestDto friendRequestDto) {
        log.info("User is responding to a friend request with ID {}", friendRequestDto.getRequestId());
        AnswerFriendResponseDto response = friendRequestService.respondToFriendRequest(friendRequestDto);
        log.info("Successfully responded to friend request with ID {}", friendRequestDto.getRequestId());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@Valid @RequestBody DeleteFriendRequest deleteFriendRequest) {
        log.info("User is deleting a friend with Email {}", deleteFriendRequest.getFriendEmail());
        friendRequestService.deleteFriend(deleteFriendRequest);
        log.info("Successfully deleted friend with Email {}", deleteFriendRequest.getFriendEmail());
    }
}
