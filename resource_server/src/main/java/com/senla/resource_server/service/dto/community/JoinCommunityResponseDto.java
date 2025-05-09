package com.senla.resource_server.service.dto.community;

import com.senla.resource_server.service.dto.message.CommunityMessageDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinCommunityResponseDto {

    @NotBlank(message = "Community name must not be blank")
    private String communityName;

    @NotNull(message = "Messages list must not be null")
    private List<CommunityMessageDto> messages;
}
