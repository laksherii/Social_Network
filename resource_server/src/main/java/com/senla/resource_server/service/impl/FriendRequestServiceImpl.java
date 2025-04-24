package com.senla.resource_server.service.impl;


import com.senla.resource_server.data.dao.impl.FriendRequestDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.mapper.FriendRequestMapper;
import com.senla.resource_server.exception.BadRequestParamException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestServiceImpl {

    private final UserDaoImpl userDao;
    private final FriendRequestDaoImpl friendRequestDao;
    private final FriendRequestMapper friendRequestMapper;

    public SendFriendResponseDto sendFriendRequest(SendFriendRequestDto friendRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();

        if (friendRequestDto.getReceiverEmail().equals(senderEmail)) {
            throw new BadRequestParamException("Нельзя отправить запрос самому себе.");
        }

        User sender = userDao.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Отправитель не найден"));

        User recipient = userDao.findByEmail(friendRequestDto.getReceiverEmail())
                .orElseThrow(() -> new EntityNotFoundException("Получатель не найден"));

        Optional<FriendRequest> existingRequestOpt = friendRequestDao.findBySenderAndRecipient(sender, recipient);

        if (existingRequestOpt.isPresent()) {
            FriendRequest existingRequest = existingRequestOpt.get();

            if (existingRequest.getStatus() == FriendRequestStatus.UNDEFINED) {
                throw new IllegalStateException("Запрос уже отправлен.");
            }

            if (existingRequest.getStatus() == FriendRequestStatus.ACCEPTED) {
                throw new IllegalStateException("Вы уже друзья.");
            }

            if (existingRequest.getStatus() == FriendRequestStatus.DELETED ||
                existingRequest.getStatus() == FriendRequestStatus.REJECTED) {

                existingRequest.setStatus(FriendRequestStatus.UNDEFINED);
                FriendRequest updatedRequest = friendRequestDao.save(existingRequest);
                return friendRequestMapper.toSendFriendResponseDto(updatedRequest);
            }
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequestStatus.UNDEFINED);

        FriendRequest savedRequest = friendRequestDao.save(friendRequest);
        return friendRequestMapper.toSendFriendResponseDto(savedRequest);
    }


    public AnswerFriendResponseDto respondToFriendRequest(AnswerFriendRequestDto answerRequestDto) {
        FriendRequest request = friendRequestDao.findById(answerRequestDto.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User recipient = userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));

        if (!recipient.getEmail().equals(request.getRecipient().getEmail()) ||
            request.getStatus() != FriendRequestStatus.UNDEFINED) {
            throw new IllegalStateException("Запрос уже обработан или пользователь не может ответить на запрос");
        }

        FriendRequestStatus status = answerRequestDto.getStatus();
        request.setStatus(status);

        if (status == FriendRequest.FriendRequestStatus.ACCEPTED) {
            addFriend(request);
        }
        FriendRequest save = friendRequestDao.save(request);
        return friendRequestMapper.toAnswerFriendResponseDto(save);
    }

    private void addFriend(FriendRequest request) {
        User sender = request.getSender();
        User recipient = request.getRecipient();
        sender.getFriends().add(recipient);
        recipient.getFriends().add(sender);

        userDao.save(sender);
        userDao.save(recipient);
    }

    public void deleteFriend(DeleteFriendRequest friendRequestDto) {
        FriendRequest request = friendRequestDao.findById(friendRequestDto.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUser = authentication.getName();

        User user = userDao.findByEmail(emailUser)
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));

        User friend = userDao.findByEmail(friendRequestDto.getFriendEmail())
                .orElseThrow(() -> new EntityNotFoundException("Not found User"));

        boolean isFriend = user.getFriends().stream()
                .anyMatch(usr -> usr.getEmail().equals(friend.getEmail()));

        if (isFriend && request.getStatus() == FriendRequestStatus.ACCEPTED) {
            request.setStatus(FriendRequestStatus.DELETED);
            User sender = request.getSender();
            User recipient = request.getRecipient();
            sender.getFriends().remove(recipient);
            recipient.getFriends().remove(sender);

            userDao.save(sender);
            userDao.save(recipient);
        }
        friendRequestDao.save(request);
    }
}

