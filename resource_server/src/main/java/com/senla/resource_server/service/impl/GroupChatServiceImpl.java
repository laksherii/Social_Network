package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.impl.GroupChatDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.mapper.GroupChatMapper;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupChatServiceImpl {

    private final GroupChatDaoImpl groupDaoImpl;
    private final GroupChatMapper groupChatMapper;
    private final UserDaoImpl userDaoImpl;

    public CreateGroupChatResponseDto create(CreateGroupChatRequestDto groupDto) {
// todo Добавить id создателя, когда будет jwt token
        Set<User> users = groupDto.getUserIds().stream()
                .map(userDaoImpl::findById)
                .filter(Optional::isPresent)
                .distinct()
                .map(Optional::get)
                .collect(Collectors.toSet());

        GroupChat groupChat = new GroupChat();
        groupChat.setUsers(users);
        groupChat.setName(groupDto.getName());
        GroupChat save = groupDaoImpl.save(groupChat);
        return groupChatMapper.toCreateGroupDtoResponse(save);
    }
}
