package com.senla.resource_server.service.dto.community;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class JoinCommunityRequestDto {

    @NotNull(message = "Community ID must not be null")
    private Long communityId;
}
