package com.senla.resource_server.service.dto.groupChat;

import com.senla.resource_server.data.entity.GroupChatMessage;
import com.senla.resource_server.service.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GroupRequestDto {
    private String name;
    private Set<UserDto> users;
    private List<GroupChatMessage> messages;
}
