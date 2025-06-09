package com.senla.resource_server.service.dto.friendRequest;

import jakarta.validation.constraints.Email;
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
public class DeleteFriendRequest {

    @NotBlank(message = "Friend email must not be blank")
    @Email(message = "Friend email must be a valid email address")
    private String friendEmail;
}
