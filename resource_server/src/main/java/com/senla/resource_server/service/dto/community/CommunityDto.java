package com.senla.resource_server.service.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CommunityDto {

    @NotBlank(message = "Community name must not be blank")
    private String name;
}
