package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.GroupChatMessage;

import java.util.List;

public interface GroupChatMessageDao {
    GroupChatMessage save(GroupChatMessage message);

    List<GroupChatMessage> findByGroup(GroupChat groupChat);
}
