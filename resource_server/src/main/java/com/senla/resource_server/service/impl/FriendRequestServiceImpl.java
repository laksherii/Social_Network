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
import com.senla.resource_server.service.interfaces.FriendRequestService;
import com.senla.resource_server.service.mapper.FriendRequestMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final UserDao userDao;
    private final FriendRequestDao friendRequestDao;
    private final FriendRequestMapper friendRequestMapper;

    @Override
    public SendFriendResponseDto sendFriendRequest(SendFriendRequestDto friendRequestDto) {
        log.info("Starting to send friend request to {}", friendRequestDto.getReceiverEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        log.info("Authenticated sender: {}", senderEmail);

        if (friendRequestDto.getReceiverEmail().equals(senderEmail)) {
            log.info("Sender and receiver are the same: {}", senderEmail);
            throw new IllegalArgumentException("You cannot send a request to yourself");
        }

        User sender = userDao.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Sender friend request not found."));
        log.info("Sender found: {}", sender.getEmail());

        User recipient = userDao.findByEmail(friendRequestDto.getReceiverEmail())
                .orElseThrow(() -> new EntityNotFoundException("Recipient friend request not found."));
        log.info("Recipient found: {}", recipient.getEmail());

        FriendRequest existingRequest = friendRequestDao.findBySenderAndRecipient(sender, recipient)
                .orElse(null);

        boolean alreadyFriend = recipient.getFriends().stream()
                .map(User::getEmail)
                .collect(Collectors.toSet())
                .contains(sender.getEmail());

        if (alreadyFriend) {
            throw new EntityExistException("User already friend");
        }

        if (existingRequest != null) {
            log.info("Existing friend request found between {} and {}", sender.getEmail(), recipient.getEmail());
            if (existingRequest.getStatus() == FriendRequestStatus.UNDEFINED) {
                throw new IllegalStateException("Friend request has already been sent");
            }
        }
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
        log.info("Responding to friend request ID: {}", answerRequestDto.getRequestId());

        FriendRequest request = friendRequestDao.findById(answerRequestDto.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        log.info("Friend request found between {} and {}", request.getSender().getEmail(), request.getRecipient().getEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("Authenticated user: {}", email);

        User recipient = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));
        log.info("Recipient user found: {}", recipient.getEmail());

        if (!recipient.getEmail().equals(request.getRecipient().getEmail())) {
            log.info("Invalid friend request handling attempt by {}", recipient.getEmail());
            throw new IllegalStateException("The request has already been processed or the user is not authorised to process the request");
        }

        FriendRequestStatus status = answerRequestDto.getStatus();
        request.setStatus(status);

        AnswerFriendResponseDto responseDto = friendRequestMapper.toAnswerFriendResponseDto(request);

        if (status == FriendRequestStatus.ACCEPTED) {
            addFriend(request);
            log.info("Users {} and {} are now friends", request.getSender().getEmail(), request.getRecipient().getEmail());
            friendRequestDao.delete(answerRequestDto.getRequestId());
            log.info("Deleted friend request from {} to {} with status {}", request.getSender().getEmail(), request.getRecipient().getEmail(), status);
        } else if (status == FriendRequestStatus.REJECTED) {
            friendRequestDao.delete(answerRequestDto.getRequestId());
            log.info("Deleted friend request from {} to {} with Status {}", request.getSender().getEmail(), request.getRecipient().getEmail(), status);
        }
        return responseDto;
    }

    private void addFriend(FriendRequest request) {
        log.info("Adding friends: {} and {}", request.getSender().getEmail(), request.getRecipient().getEmail());

        User sender = request.getSender();
        User receiver = request.getRecipient();
        sender.getFriends().add(receiver);
        userDao.save(sender);
        receiver.getFriends().add(sender);
        userDao.save(receiver);
    }

    @Override
    public void deleteFriend(DeleteFriendRequest friendRequestDto) {
        log.info("Starting to delete friend with email: {}", friendRequestDto.getFriendEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUser = authentication.getName();
        log.info("Authenticated user: {}", emailUser);

        User user = userDao.findByEmail(emailUser)
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));
        log.info("User found: {}", user.getEmail());

        Set<String> friends = user.getFriends().stream()
                .map(User::getEmail)
                .filter(email -> email.equals(friendRequestDto.getFriendEmail()))
                .collect(Collectors.toSet());

        if (!friends.contains(friendRequestDto.getFriendEmail())) {
            throw new UserNotYourFriendException("User wit email: " + friendRequestDto.getFriendEmail() + " not your friend ");
        }

        User friend = userDao.findByEmail(friendRequestDto.getFriendEmail())
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));
        log.info("Friend found: {}", friend.getEmail());

        user.getFriends().remove(friend);
        userDao.save(friend);
        log.info("User {} deleted from friend request", friend.getEmail());
        friend.getFriends().remove(user);
        userDao.save(user);
        log.info("User {} deleted from friend request", friend.getEmail());
    }
}

