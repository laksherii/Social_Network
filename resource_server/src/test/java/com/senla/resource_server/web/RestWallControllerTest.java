package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.service.dto.message.WallMessageDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import com.senla.resource_server.service.impl.WallServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestWallController.class)
@WithMockUser(roles = "ADMIN")
class RestWallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WallServiceImpl wallService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateWall_WhenValidRequest_ShouldReturnUpdatedWall() throws Exception {
        // Given
        WallRequestDto requestDto = WallRequestDto.builder()
                .message("Hala Madrid")
                .build();
        UserDto owner = new UserDto("owner@example.com");
        WallMessageDto message = new WallMessageDto("New wall message");

        WallResponseDto responseDto = WallResponseDto.builder()
                .owner(owner)
                .messages(List.of(message))
                .build();

        given(wallService.updateWall(any(WallRequestDto.class)))
                .willReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/wall")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.email").value("owner@example.com"))
                .andExpect(jsonPath("$.messages[0].message").value("New wall message"));
    }

    @Test
    void updateWall_ShouldReturnBadRequest_WhenMessageIsBlank() throws Exception {
        // Given
        WallRequestDto invalidRequest = WallRequestDto.builder()
                .message("")
                .build();

        // When & Then
        mockMvc.perform(put("/wall")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}

