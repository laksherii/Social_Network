package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.impl.GroupChatDaoImpl;
import com.senla.resource_server.data.dao.impl.GroupChatMessageDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.GroupChatMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.mapper.MessageMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequest;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupChatMessageServiceImpl {
    private final GroupChatMessageDaoImpl groupChatMessageDao;
    private final GroupChatDaoImpl groupChatDaoImpl;
    private final UserDaoImpl userDaoImpl;
    private final MessageMapper messageMapper;

    //todo Взять отправителя с jwt токена и установить в отправителя
    public GroupChatMessageResponse sendGroupChatMessage(GroupChatMessageRequest groupChatMessage) {
        GroupChat groupChat = groupChatDaoImpl.findById(groupChatMessage.getGroupId());
        List<GroupChatMessage> messages = groupChat.getMessages();
        User user = userDaoImpl.findById(1L).orElseThrow(() -> new EntityNotFoundException("No such user"));

        GroupChatMessage groupMessage = new GroupChatMessage();
        groupMessage.setMessage(groupChatMessage.getMessage());
        groupMessage.setSender(user);
        groupMessage.setGroupChat(groupChat);

        messages.add(groupMessage);

        groupChat.setMessages(messages);
        GroupChatMessage save = groupChatMessageDao.save(groupMessage);
        return messageMapper.toGroupChatMessageResponse(save);
    }
}
