package com.senla.resource_server.service.impl;


import com.senla.resource_server.data.dao.FriendRequestDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.interfaces.FriendRequestService;
import com.senla.resource_server.service.mapper.FriendRequestMapper;
import com.senla.resource_server.exception.BadRequestParamException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalArgumentException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
            throw new IllegalArgumentException("Нельзя отправить запрос самому себе.");
        }

        User sender = userDao.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Sender friend request not found."));
        log.info("Sender found: {}", sender.getEmail());

        User recipient = userDao.findByEmail(friendRequestDto.getReceiverEmail())
                .orElseThrow(() -> new EntityNotFoundException("Recipient friend request not found."));
        log.info("Recipient found: {}", recipient.getEmail());

        FriendRequest existingRequest = friendRequestDao.findBySenderAndRecipient(sender, recipient)
                .orElse(null);

        if (existingRequest != null) {
            log.info("Existing friend request found between {} and {}", sender.getEmail(), recipient.getEmail());

            if (existingRequest.getStatus() == FriendRequestStatus.UNDEFINED) {
                throw new IllegalStateException("Friend request has already been sent");
            }

            if (existingRequest.getStatus() == FriendRequestStatus.ACCEPTED) {
                throw new IllegalStateException("You are already friends");
            }

            if (existingRequest.getStatus() == FriendRequestStatus.DELETED ||
                existingRequest.getStatus() == FriendRequestStatus.REJECTED) {

                existingRequest.setStatus(FriendRequestStatus.UNDEFINED);
                FriendRequest updatedRequest = friendRequestDao.save(existingRequest);
                log.info("Friend request status reset to UNDEFINED between {} and {}", sender.getEmail(), recipient.getEmail());
                return friendRequestMapper.toSendFriendResponseDto(updatedRequest);
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

        if (!recipient.getEmail().equals(request.getRecipient().getEmail()) ||
            request.getStatus() != FriendRequestStatus.UNDEFINED) {
            log.info("Invalid friend request handling attempt by {}", recipient.getEmail());
            throw new IllegalStateException("The request has already been processed or the user is not authorised to process the request");
        }

        FriendRequestStatus status = answerRequestDto.getStatus();
        request.setStatus(status);
        log.info("Friend request status updated to {} by {}", status, recipient.getEmail());

        if (status == FriendRequestStatus.ACCEPTED) {
            addFriend(request);
            log.info("Users {} and {} are now friends", request.getSender().getEmail(), request.getRecipient().getEmail());
        }

        FriendRequest savedRequest = friendRequestDao.save(request);
        log.info("Friend request response saved for request ID: {}", savedRequest.getId());
        return friendRequestMapper.toAnswerFriendResponseDto(savedRequest);
    }

    private void addFriend(FriendRequest request) {
        log.info("Adding friends: {} and {}", request.getSender().getEmail(), request.getRecipient().getEmail());

        User sender = request.getSender();
        User receiver = request.getRecipient();
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userDao.save(sender);
        userDao.save(receiver);
    }

    @Override
    public void deleteFriend(DeleteFriendRequest friendRequestDto) {
        log.info("Starting to delete friend with email: {}", friendRequestDto.getFriendEmail());

        FriendRequest request = friendRequestDao.findById(friendRequestDto.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));

        log.info("Friend request found for deletion between {} and {}",
                request.getSender().getEmail(), request.getRecipient().getEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUser = authentication.getName();
        log.info("Authenticated user: {}", emailUser);

        String senderEmail = request.getSender().getEmail();
        String recipientEmail = request.getRecipient().getEmail();
        boolean isParticipant = emailUser.equals(senderEmail) || emailUser.equals(recipientEmail);
        boolean isAccepted = request.getStatus() == FriendRequestStatus.ACCEPTED;

        if (!isParticipant || !isAccepted) {
            throw new IllegalArgumentException("It's not your friend");
        }

        User user = userDao.findByEmail(emailUser)
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));
        log.info("User found: {}", user.getEmail());

        User friend = userDao.findByEmail(friendRequestDto.getFriendEmail())
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));
        log.info("Friend found: {}", friend.getEmail());

        request.setStatus(FriendRequestStatus.DELETED);

        User sender = request.getSender();
        User recipient = request.getRecipient();

        sender.getFriends().remove(recipient);
        recipient.getFriends().remove(sender);

        userDao.save(sender);
        userDao.save(recipient);
        log.info("Users {} and {} are no longer friends", sender.getEmail(), recipient.getEmail());

        friendRequestDao.save(request);
        log.info("Friend request status updated to DELETED for request ID: {}", request.getId());
    }
}

