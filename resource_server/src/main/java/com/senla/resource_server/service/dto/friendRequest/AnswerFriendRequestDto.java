package com.senla.resource_server.service.dto.friendRequest;

import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerFriendRequestDto {

    @NotNull(message = "Request ID must not be null")
    private Long requestId;

    @NotNull(message = "Status must not be null")
    private FriendRequestStatus status;
}
