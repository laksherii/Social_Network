package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import com.senla.resource_server.service.impl.FriendRequestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend-request")
public class RestFriendRequestController {

    private final FriendRequestServiceImpl friendRequestService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SendFriendResponseDto sendFriendRequest(@RequestBody SendFriendRequestDto friendRequestDto) {
        return friendRequestService.sendFriendRequest(friendRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public AnswerFriendResponseDto responseFriendRequest(@RequestBody AnswerFriendRequestDto friendRequestDto) {
        return friendRequestService.respondToFriendRequest(friendRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@RequestBody DeleteFriendRequest deleteFriendRequest) {
        friendRequestService.deleteFriend(deleteFriendRequest);
    }
}
