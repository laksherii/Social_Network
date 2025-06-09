package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.GroupChat;

import java.util.Optional;

public interface GroupChatDao {
    Optional<GroupChat> findById(Long id);

    GroupChat save(GroupChat groupChat);
}
