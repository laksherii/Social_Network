package com.senla.resource_server.service.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatMessageRequestDto {

    @NotNull(message = "Id must not be null")
    private Long groupId;

    @NotBlank(message = "Content must not be blank")
    private String content;
}
