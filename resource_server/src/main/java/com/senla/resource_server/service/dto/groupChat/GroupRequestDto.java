package com.senla.resource_server.service.dto.groupChat;

import com.senla.resource_server.data.entity.GroupChatMessage;
import com.senla.resource_server.service.dto.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class GroupRequestDto {

    @NotBlank(message = "Group name must not be blank")
    private String name;

    @NotNull(message = "Users set must not be null")
    @NotEmpty(message = "Users set must not be empty")
    @Valid
    private Set<UserDto> users;

    @NotNull(message = "Messages list must not be null")
    @Valid
    private List<GroupChatMessage> messages;
}
