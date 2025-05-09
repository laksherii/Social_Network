package com.senla.resource_server.service.dto.community;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinCommunityRequestDto {

    @NotNull(message = "Community ID must not be null")
    private Long communityId;
}
