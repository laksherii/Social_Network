package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.service.dto.user.UserAuthRequestDto;
import com.senla.resource_server.service.dto.user.UserAuthResponseDto;
import com.senla.resource_server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RestAuthController.class)
@WithMockUser(username = "test@example.com", roles = "USER")
class RestAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticate_WhenValidCredentials_ShouldReturnUser() throws Exception {
        // Given
        UserAuthRequestDto request = new UserAuthRequestDto();
        request.setEmail("user@example.com");
        request.setPassword("secret");

        UserAuthResponseDto response = new UserAuthResponseDto();
        response.setId(1L);
        response.setEmail("user@example.com");
        response.setRole(RoleType.ROLE_USER);

        given(userService.authenticate(request)).willReturn(response);

        // When / Then
        mockMvc.perform(post("/authenticate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value(RoleType.ROLE_USER.toString()));
    }
}
