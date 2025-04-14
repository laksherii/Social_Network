package com.senla.social_network.data.dao;

import com.senla.social_network.data.entity.FriendRequest;
import com.senla.social_network.data.entity.User;

import java.util.Optional;

public interface FriendRequestDao {
    void save(FriendRequest request);

    boolean existsBySenderAndRecipient(User sender, User recipient);

    Optional<FriendRequest> findById(Long id);

    Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient);
}
