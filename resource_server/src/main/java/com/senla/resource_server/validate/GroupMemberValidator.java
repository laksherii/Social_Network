package com.senla.resource_server.validate;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.GroupChatMessage;
import com.senla.resource_server.data.entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class GroupMemberValidator implements ConstraintValidator<ValidGroupMember, GroupChatMessage> {

    @Override
    public boolean isValid(GroupChatMessage message, ConstraintValidatorContext context) {
        if (message == null) {
            return true;
        }

        GroupChat groupChat = message.getGroupChat();
        User sender = message.getSender();

        if (groupChat == null || groupChat.getUsers() == null || sender == null) {
            return false;
        }

        return groupChat.getUsers().contains(sender);
    }
}

