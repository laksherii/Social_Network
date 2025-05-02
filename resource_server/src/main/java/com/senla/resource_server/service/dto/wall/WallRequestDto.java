package com.senla.resource_server.service.dto.wall;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class WallRequestDto {

    @NotBlank(message = "Message must not be blank")
    @Size(max = 1000, message = "Message must not exceed 500 characters")
    private String message;
}
