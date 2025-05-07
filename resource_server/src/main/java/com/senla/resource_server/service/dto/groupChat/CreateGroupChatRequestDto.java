package com.senla.resource_server.service.dto.groupChat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupChatRequestDto {

    @NotBlank(message = "Group chat name must not be blank")
    private String name;

    @NotNull(message = "User IDs set must not be null")
    @NotEmpty(message = "User IDs set must not be empty")
    private Set<Long> userIds;
}
