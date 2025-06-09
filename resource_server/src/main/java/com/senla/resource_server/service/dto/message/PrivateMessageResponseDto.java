package com.senla.resource_server.service.dto.message;

import com.senla.resource_server.service.dto.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessageResponseDto {

    @Valid
    private UserDto sender;

    @NotBlank(message = "Message content must not be blank")
    @Size(max = 1000, message = "Message content must not exceed 1000 characters")
    private String content;
}
