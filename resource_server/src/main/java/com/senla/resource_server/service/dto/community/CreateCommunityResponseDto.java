package com.senla.resource_server.service.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommunityResponseDto {

    @NotBlank(message = "Community name must not be blank")
    private String communityName;
}
