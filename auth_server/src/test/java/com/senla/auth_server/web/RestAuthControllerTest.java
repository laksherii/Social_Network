package com.senla.auth_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.auth_server.service.dto.AuthDtoRequest;
import com.senla.auth_server.service.dto.AuthDtoResponse;
import com.senla.auth_server.service.dto.RefreshDtoToken;
import com.senla.auth_server.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestAuthController.class)
@Import(TestConfig.class)
class RestAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthServiceImpl authService;

    private AuthDtoRequest validLoginRequest;
    private RefreshDtoToken validRefreshRequest;
    private AuthDtoResponse authResponse;

    @BeforeEach
    void setUp() {
        validLoginRequest = AuthDtoRequest.builder()
                .email("user@example.com")
                .password("password123")
                .build();

        validRefreshRequest = RefreshDtoToken.builder()
                .refreshToken("valid-refresh-token")
                .build();

        authResponse = AuthDtoResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .build();
    }

    @Test
    void login_WithValidRequest_ReturnsAuthResponse() throws Exception {
        Mockito.when(authService.login(any(AuthDtoRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.getRefreshToken()));

    }

    @Test
    void login_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        AuthDtoRequest invalidRequest = AuthDtoRequest.builder()
                .email("invalid-email")
                .password("")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_WithValidRequest_ReturnsAuthResponse() throws Exception {
        Mockito.when(authService.refresh(any(RefreshDtoToken.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRefreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.getRefreshToken()));

        verify(authService).refresh(any(RefreshDtoToken.class));
    }

    @Test
    void refresh_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        RefreshDtoToken invalidRequest = RefreshDtoToken.builder()
                .refreshToken("")
                .build();

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
