package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.dao.GroupChatDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {
    private final CommunityDao communityDao;
    private final GroupChatDao groupChatDao;

    public Community verifyUserIsCommunityAdmin(Long communityId, User user) {
        Community community = communityDao.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        if (!community.getAdmin().equals(user)) {
            throw new UserNotAdminInGroupException("User is not admin in community");
        }
        return community;
    }

    public GroupChat verifyUserIsGroupChatMember(Long groupId, User user) {
        GroupChat groupChat = groupChatDao.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group chat not found"));

        if (groupChat.getUsers().stream().noneMatch(u -> u.equals(user))) {
            throw new UserNotInGroupChatException("User not in group chat");
        }
        return groupChat;
    }
}

