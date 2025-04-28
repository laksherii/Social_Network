package com.senla.resource_server.service.dto.community;

import com.senla.resource_server.service.dto.message.CommunityMessageDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class JoinCommunityResponseDto {

    @NotBlank(message = "Community name must not be blank")
    private String communityName;

    @NotNull(message = "Messages list must not be null")
    private List<CommunityMessageDto> messages;
}
