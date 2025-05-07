package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.DeleteFriendRequest;
import com.senla.resource_server.service.dto.friendRequest.SendFriendRequestDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.impl.FriendRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestFriendRequestController.class)
@WithMockUser
class RestFriendRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendRequestServiceImpl friendRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendFriendRequest_shouldReturnCreated() throws Exception {
        // Given
        SendFriendRequestDto requestDto = new SendFriendRequestDto("receiver@example.com");
        SendFriendResponseDto responseDto = new SendFriendResponseDto(new UserDto(), FriendRequestStatus.UNDEFINED);

        given(friendRequestService.sendFriendRequest(requestDto)).willReturn(responseDto);

        // When / Then
        mockMvc.perform(post("/friend-request")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(FriendRequestStatus.UNDEFINED.toString()))
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    void responseFriendRequest_shouldReturnOk() throws Exception {
        // Given
        AnswerFriendRequestDto requestDto = new AnswerFriendRequestDto(1L, FriendRequestStatus.ACCEPTED);
        AnswerFriendResponseDto responseDto = new AnswerFriendResponseDto(new UserDto(),FriendRequestStatus.ACCEPTED);

        given(friendRequestService.respondToFriendRequest(requestDto)).willReturn(responseDto);

        // When / Then
        mockMvc.perform(put("/friend-request")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(FriendRequestStatus.ACCEPTED.toString()))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void deleteFriend_shouldReturnNoContent() throws Exception {
        // Given
        DeleteFriendRequest requestDto = new DeleteFriendRequest(1L, "friend@example.com");

        // When / Then
        mockMvc.perform(delete("/friend-request")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());
    }
}
