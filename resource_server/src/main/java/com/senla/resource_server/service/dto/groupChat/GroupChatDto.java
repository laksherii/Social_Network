package com.senla.resource_server.service.dto.groupChat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatDto {

    @NotBlank(message = "Group chat name must not be blank")
    private String name;
}
