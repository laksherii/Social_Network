package com.senla.resource_server.service.dto.groupChat;

import com.senla.resource_server.service.dto.message.GroupChatMessageDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatDto {

    @NotBlank(message = "Group chat name must not be blank")
    private String name;

    @NotNull(message = "Content must not be null")
    private List<GroupChatMessageDto> messages;
}
