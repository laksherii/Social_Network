package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;

import java.util.List;
import java.util.Optional;

public interface GroupChatDao {
    List<GroupChat> findByUser(User user);

    Optional<GroupChat> findById(Long id);

    GroupChat save(GroupChat groupChat);
}
