package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;

import java.util.List;

public interface GroupChatDao {
    List<GroupChat> findByUser(User user);
}
