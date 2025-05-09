package com.senla.resource_server.service.dto.wall;

import com.senla.resource_server.service.dto.message.WallMessageDto;
import com.senla.resource_server.service.dto.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WallResponseDto {

    @Valid
    @NotNull(message = "Owner must not be null")
    private UserDto owner;

    @Valid
    @NotNull(message = "Messages list must not be null")
    private List<WallMessageDto> messages;
}
