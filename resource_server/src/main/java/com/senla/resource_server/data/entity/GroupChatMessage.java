package com.senla.resource_server.data.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import validate.ValidGroupMember;

@Entity
@Getter
@Setter
@Table(name = "group_chat_message")
@PrimaryKeyJoinColumn(name = "message_id")
@DiscriminatorValue("GROUP")
@ValidGroupMember
public class GroupChatMessage extends Message {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull(message = "Group chat must not be null")
    private GroupChat groupChat;
}
