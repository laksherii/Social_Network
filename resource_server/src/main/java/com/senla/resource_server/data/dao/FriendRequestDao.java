package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.User;

import java.util.Optional;

public interface FriendRequestDao {
    FriendRequest save(FriendRequest request);

    Optional<FriendRequest> findById(Long id);

    Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient);

    void delete(Long id);
}
