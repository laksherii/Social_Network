package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;

import java.util.List;

public interface PrivateMessageDao {
    PrivateMessage save(PrivateMessage message);

    List<PrivateMessage> findBySenderAndRecipient(User sender, User recipient);
}
