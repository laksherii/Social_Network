package com.senla.resource_server.service.dto.user;

import com.senla.resource_server.data.entity.User.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleUserDtoRequest {

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Role must not be blank")
    private RoleType role;
}
