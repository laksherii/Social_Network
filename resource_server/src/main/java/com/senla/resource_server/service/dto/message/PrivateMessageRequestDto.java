package com.senla.resource_server.service.dto.message;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessageRequestDto {

    @Email(message = "Email must be valid")
    private String recipientEmail;

    @NotBlank(message = "Message content must not be blank")
    @Size(max = 1000, message = "Message content must not exceed 1000 characters")
    private String message;
}
