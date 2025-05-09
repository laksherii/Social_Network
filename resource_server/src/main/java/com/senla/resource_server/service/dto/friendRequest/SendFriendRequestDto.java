package com.senla.resource_server.service.dto.friendRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendFriendRequestDto {

    @NotBlank(message = "Receiver email must not be blank")
    @Email(message = "Receiver email must be a valid email address")
    private String receiverEmail;
}
