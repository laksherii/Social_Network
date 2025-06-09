package com.senla.auth_server.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshDtoToken {

    @NotBlank(message = "refreshToken must not be blank")
    private String refreshToken;
}
