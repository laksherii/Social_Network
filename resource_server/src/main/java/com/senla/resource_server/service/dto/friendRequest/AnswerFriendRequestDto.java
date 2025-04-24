package com.senla.resource_server.service.dto.friendRequest;

import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AnswerFriendRequestDto {
    private Long requestId;
    private FriendRequestStatus status;
}
