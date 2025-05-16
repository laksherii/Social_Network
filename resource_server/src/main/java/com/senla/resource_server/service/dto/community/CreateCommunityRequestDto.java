package com.senla.resource_server.service.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommunityRequestDto {

    @NotBlank(message = "Community name must not be blank")
    private String name;

    @NotBlank(message = "Description must not be blank")
    private String description;
}
