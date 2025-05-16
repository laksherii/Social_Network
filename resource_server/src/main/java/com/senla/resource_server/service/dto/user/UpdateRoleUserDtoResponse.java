package com.senla.resource_server.service.dto.user;

import com.senla.resource_server.data.entity.User.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleUserDtoResponse {

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role must not be null")
    private RoleType role;
}
