package com.senla.auth_server.service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String email;
    private String role;
}
