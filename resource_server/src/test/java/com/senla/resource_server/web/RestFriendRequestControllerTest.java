package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.interfaces.FriendRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestFriendRequestController.class)
@Import(TestConfig.class)
class RestFriendRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendRequestService friendRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void sendFriendRequest_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        SendFriendRequestDto request = new SendFriendRequestDto("friend@example.com");
        UserDto userDto = UserDto.builder().email("friend@example.com").build();
        SendFriendResponseDto response = new SendFriendResponseDto(userDto, FriendRequestStatus.UNDEFINED);

        given(friendRequestService.sendFriendRequest(any())).willReturn(response);

        // When & Then
        mockMvc.perform(post("/friend-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipient.email").value("friend@example.com"))
                .andExpect(jsonPath("$.status").value(FriendRequestStatus.UNDEFINED.toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void sendFriendRequest_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Given
        SendFriendRequestDto invalidRequest = new SendFriendRequestDto("invalid-email");

        // When & Then
        mockMvc.perform(post("/friend-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void responseFriendRequest_Accept_ReturnsOk() throws Exception {
        // Given
        AnswerFriendRequestDto request = new AnswerFriendRequestDto(1L, FriendRequestStatus.ACCEPTED);
        UserDto userDto = UserDto.builder().email("sender@example.com").build();
        AnswerFriendResponseDto response = new AnswerFriendResponseDto(userDto, FriendRequestStatus.ACCEPTED);

        given(friendRequestService.respondToFriendRequest(any())).willReturn(response);

        // When & Then
        mockMvc.perform(put("/friend-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender.email").value("sender@example.com"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void responseFriendRequest_Reject_ReturnsOk() throws Exception {
        // Given
        AnswerFriendRequestDto request = new AnswerFriendRequestDto(1L, FriendRequestStatus.REJECTED);
        UserDto userDto = UserDto.builder().email("sender@example.com").build();
        AnswerFriendResponseDto response = new AnswerFriendResponseDto(userDto, FriendRequestStatus.REJECTED);

        given(friendRequestService.respondToFriendRequest(any())).willReturn(response);

        // When & Then
        mockMvc.perform(put("/friend-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void responseFriendRequest_MissingStatus_ReturnsBadRequest() throws Exception {
        // Given
        String invalidJson = "{\"requestId\": 1}";

        // When & Then
        mockMvc.perform(put("/friend-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteFriend_ValidRequest_ReturnsNoContent() throws Exception {
        // Given
        DeleteFriendRequest request = new DeleteFriendRequest("friend@example.com");
        doNothing().when(friendRequestService).deleteFriend(any());

        // When & Then
        mockMvc.perform(delete("/friend-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteFriend_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Given
        DeleteFriendRequest invalidRequest = new DeleteFriendRequest("invalid-email");

        // When & Then
        mockMvc.perform(delete("/friend-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}