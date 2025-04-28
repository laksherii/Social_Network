package com.senla.resource_server.service.dto.community;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindCommunityRequestDto {

    @NotNull(message = "Community ID must not be null")
    private Long communityId;
}
