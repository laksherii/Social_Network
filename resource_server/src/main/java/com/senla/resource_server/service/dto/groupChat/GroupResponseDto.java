package com.senla.resource_server.service.dto.groupChat;

import com.senla.resource_server.service.dto.message.GroupChatMessageDto;
import com.senla.resource_server.service.dto.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDto {
    @NotBlank(message = "Group name must not be blank")
    private String name;

    @NotNull(message = "Users set must not be null")
    @NotEmpty(message = "Users set must not be empty")
    @Valid
    private Set<UserDto> users;

    @NotNull(message = "Messages list must not be null")
    @Valid
    private List<GroupChatMessageDto> messages;
}
