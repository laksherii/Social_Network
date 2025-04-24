package com.senla.resource_server.service.dto.friendRequest;

import com.senla.resource_server.service.dto.user.UserDto;
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
public class SendFriendResponseDto {
    private UserDto sender;
    private String status;
}
