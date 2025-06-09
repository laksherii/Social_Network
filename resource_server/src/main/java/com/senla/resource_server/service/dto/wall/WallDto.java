package com.senla.resource_server.service.dto.wall;

import com.senla.resource_server.service.dto.message.WallMessageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WallDto {

    @Valid
    @NotNull(message = "Messages list must not be null")
    private List<WallMessageDto> messages;
}
