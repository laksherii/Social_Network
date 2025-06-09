package com.senla.resource_server.service.dto.groupChat;

import com.senla.resource_server.service.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupChatResponseDto {

    @NotNull(message = "Group chat ID must not be null")
    private Long id;

    @NotBlank(message = "Group chat name must not be blank")
    private String name;

    @NotNull(message = "Users set must not be null")
    @NotEmpty(message = "Users set must not be empty")
    private Set<UserDto> users;
}
