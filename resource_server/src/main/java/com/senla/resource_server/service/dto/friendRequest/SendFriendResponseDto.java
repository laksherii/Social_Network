package com.senla.resource_server.service.dto.friendRequest;

import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.service.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendFriendResponseDto {

    @NotNull(message = "Sender must not be null")
    private UserDto sender;

    @NotBlank(message = "Status must not be blank")
    private FriendRequestStatus status;
}