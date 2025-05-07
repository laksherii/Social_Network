package com.senla.resource_server.service.dto.friendRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFriendRequest {

    @NotNull(message = "Request ID must not be null")
    private Long requestId;

    @NotBlank(message = "Friend email must not be blank")
    @Email(message = "Friend email must be a valid email address")
    private String friendEmail;
}
