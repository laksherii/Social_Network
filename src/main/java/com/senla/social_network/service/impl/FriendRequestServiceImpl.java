package com.senla.social_network.service.impl;


import com.senla.social_network.data.dao.impl.FriendRequestDaoImpl;
import com.senla.social_network.data.dao.impl.UserDaoImpl;
import com.senla.social_network.data.entity.FriendRequest;
import com.senla.social_network.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.social_network.data.entity.User;
import com.senla.social_network.exception.BadRequestParamException;
import com.senla.social_network.exception.EntityNotFoundException;
import com.senla.social_network.exception.IllegalStateException;
import com.senla.social_network.service.FriendRequestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {

    private final UserDaoImpl userRepository;
    private final FriendRequestDaoImpl friendRequestRepository;

    @Override
    public void sendFriendRequest(Long senderId, Long recipientId) {
        if (senderId.equals(recipientId)) {
            throw new BadRequestParamException("Нельзя отправить запрос самому себе.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Отправитель не найден"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Получатель не найден"));

        boolean exists = friendRequestRepository.existsBySenderAndRecipient(sender, recipient);
        if (exists) {
            throw new IllegalStateException("Запрос уже отправлен.");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus(FriendRequest.FriendRequestStatus.UNDEFINED);

        friendRequestRepository.save(request);
    }

    @Override
    public void respondToFriendRequest(Long requestId, FriendRequestStatus status) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));

        if (request.getStatus() != FriendRequest.FriendRequestStatus.UNDEFINED) {
            throw new IllegalStateException("Запрос уже обработан.");
        }

        request.setStatus(status);

        if (status == FriendRequest.FriendRequestStatus.ACCEPTED) {
            User sender = request.getSender();
            User recipient = request.getRecipient();
            sender.getFriends().add(recipient);
            recipient.getFriends().add(sender);

            userRepository.save(sender);
            userRepository.save(recipient);
        }

        friendRequestRepository.save(request);
    }

    @Override
    public void deleteFriend(Long requestId, FriendRequestStatus status) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));

        if (request.getStatus() == FriendRequestStatus.ACCEPTED) {
            request.setStatus(status);
            if (status == FriendRequestStatus.DELETED) {
                User sender = request.getSender();
                User recipient = request.getRecipient();
                sender.getFriends().remove(recipient);
                recipient.getFriends().remove(sender);

                userRepository.save(sender);
                userRepository.save(recipient);
            }
            friendRequestRepository.save(request);
        } else {
            throw new BadRequestParamException("Статус отличен от ACCEPTED");
        }
    }
}

