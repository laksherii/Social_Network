package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.interfaces.GroupChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestGroupChatController.class)
@Import(TestConfig.class)
class RestGroupChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupChatService groupService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void create_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        CreateGroupChatRequestDto request = new CreateGroupChatRequestDto(
                "Dev Team",
                Set.of("user1@example.com", "user2@example.com")
        );

        CreateGroupChatResponseDto response = CreateGroupChatResponseDto.builder()
                .id(1L)
                .name("Dev Team")
                .users(Set.of(
                        new UserDto("user1@example.com"),
                        new UserDto("user2@example.com")
                ))
                .build();

        given(groupService.create(any(CreateGroupChatRequestDto.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/group-chats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Dev Team"))
                .andExpect(jsonPath("$.users.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given
        CreateGroupChatRequestDto invalidRequest = new CreateGroupChatRequestDto(
                "",
                Set.of()
        );

        // When & Then
        mockMvc.perform(post("/group-chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void findGroupChat_ValidId_ReturnsOk() throws Exception {
        // Given
        Long groupId = 1L;
        GroupChatDto response = GroupChatDto.builder()
                .name("Dev Team")
                .messages(List.of(
                        GroupChatMessageDto.builder()
                                .sender(new UserDto())
                                .content("Hello")
                                .build(),
                        GroupChatMessageDto.builder()
                                .sender(new UserDto())
                                .content("How are you?")
                                .build()
                ))
                .build();

        given(groupService.findById(groupId)).willReturn(response);

        // When & Then
        mockMvc.perform(get("/group-chats/{id}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dev Team"))
                .andExpect(jsonPath("$.messages.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findGroupChat_NotFound_ReturnsNotFound() throws Exception {
        // Given
        Long invalidId = 999L;
        given(groupService.findById(invalidId)).willThrow(new EntityNotFoundException("Group not found"));

        // When & Then
        mockMvc.perform(get("/group-chats/{id}", invalidId))
                .andExpect(status().isNotFound());
    }
}