package com.senla.resource_server.service.dto.groupChat;

import com.senla.resource_server.service.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateGroupChatResponseDto {
    private Long id;
    private String name;
    private Set<UserDto> users;
}
