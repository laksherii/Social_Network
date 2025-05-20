package com.senla.resource_server.service.impl;


import com.senla.resource_server.data.dao.FriendRequestDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.exception.UserNotYourFriendException;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import com.senla.resource_server.service.interfaces.FriendRequestService;
import com.senla.resource_server.service.mapper.FriendRequestMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final UserDao userDao;
    private final FriendRequestDao friendRequestDao;
    private final FriendRequestMapper friendRequestMapper;
    private final AuthService authService;
    private final FriendValidationService friendValidationService;

    @Override
    public SendFriendResponseDto sendFriendRequest(SendFriendRequestDto friendRequestDto) {
        User sender = authService.getCurrentUser();
        User recipient = userDao.findByEmail(friendRequestDto.getReceiverEmail())
                .orElseThrow(() -> new EntityNotFoundException("Recipient friend request not found."));

        friendValidationService.validateNotSelf(sender, recipient);
        friendValidationService.validateNotAlreadyFriends(sender, recipient);

        FriendRequest existingRequest = friendRequestDao.findBySenderAndRecipient(sender, recipient).orElse(null);
        friendValidationService.validateRequestNotSent(existingRequest);

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequestStatus.UNDEFINED);

        FriendRequest savedRequest = friendRequestDao.save(friendRequest);
        log.info("Friend request successfully created from {} to {}", sender.getEmail(), recipient.getEmail());
        return friendRequestMapper.toSendFriendResponseDto(savedRequest);
    }

    @Override
    public AnswerFriendResponseDto respondToFriendRequest(AnswerFriendRequestDto answerRequestDto) {
        FriendRequest request = friendRequestDao.findById(answerRequestDto.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        User recipient = authService.getCurrentUser();

        if (!recipient.getEmail().equals(request.getRecipient().getEmail())) {
            throw new IllegalStateException("The request has already been processed or the user is not authorised to process the request");
        }

        FriendRequestStatus status = answerRequestDto.getStatus();
        request.setStatus(status);


        if (status == FriendRequestStatus.ACCEPTED) {
            addFriend(request);
            friendRequestDao.delete(answerRequestDto.getRequestId());
        } else if (status == FriendRequestStatus.REJECTED) {
            friendRequestDao.delete(answerRequestDto.getRequestId());
        }

        log.info("Friend request (ID: {}) from '{}' to '{}' was {}",
                request.getId(),
                request.getSender().getEmail(),
                request.getRecipient().getEmail(),
                status);

        AnswerFriendResponseDto responseDto = friendRequestMapper.toAnswerFriendResponseDto(request);
        return responseDto;
    }

    private void addFriend(FriendRequest request) {
        User sender = request.getSender();
        User receiver = request.getRecipient();
        sender.getFriends().add(receiver);
        userDao.save(sender);
        receiver.getFriends().add(sender);
        userDao.save(receiver);
    }

    @Override
    public void deleteFriend(DeleteFriendRequest friendRequestDto) {
        User user = authService.getCurrentUser();

        boolean isFriend = user.getFriends().stream()
                .anyMatch(friend -> friend.getEmail().equals(friendRequestDto.getFriendEmail()));

        if (!isFriend) {
            throw new UserNotYourFriendException("User wit email: " + friendRequestDto.getFriendEmail() + " not your friend ");
        }

        User friend = userDao.findByEmail(friendRequestDto.getFriendEmail())
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));

        user.getFriends().remove(friend);
        userDao.save(friend);
        friend.getFriends().remove(user);
        userDao.save(user);
        log.info("User {} deleted from friend", friend.getEmail());
    }
}


