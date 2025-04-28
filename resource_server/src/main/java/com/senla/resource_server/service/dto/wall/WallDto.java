package com.senla.resource_server.service.dto.wall;

import com.senla.resource_server.service.dto.message.WallMessageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class WallDto {

    @Valid
    @NotNull(message = "Messages list must not be null")
    private List<WallMessageDto> messages;
}
