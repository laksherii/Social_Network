package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.service.dto.message.GetGroupChatMessageDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.message.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.impl.CommunityMessageServiceImpl;
import com.senla.resource_server.service.impl.GroupChatMessageServiceImpl;
import com.senla.resource_server.service.impl.PrivateMessageServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestMessageController.class)
@WithMockUser(roles = "ADMIN")
class RestMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrivateMessageServiceImpl privateMessageService;

    @MockitoBean
    private GroupChatMessageServiceImpl groupChatMessageService;

    @MockitoBean
    private CommunityMessageServiceImpl communityMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMessagesByRecipientEmail_ShouldReturnMessages_WhenValidRequest() throws Exception {
        // Given
        String email = "test@example.com";
        UserDto sender = new UserDto("sender@example.com");
        PrivateMessageResponseDto message = new PrivateMessageResponseDto(sender, "Hello");

        given(privateMessageService.getMessagesByRecipientEmail(email))
                .willReturn(List.of(message));

        // When & Then
        mockMvc.perform(get("/message/private").param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Hello"))
                .andExpect(jsonPath("$[0].sender.email").value("sender@example.com"));

    }

    @Test
    void sendPrivateMessage_ShouldReturnMessage_WhenValidRequest() throws Exception {
        // Given
        PrivateMessageRequestDto requestDto = new PrivateMessageRequestDto(1L, "Test message");
        UserDto sender = new UserDto("sender@example.com");
        PrivateMessageResponseDto responseDto = new PrivateMessageResponseDto(sender, "Test message");

        given(privateMessageService.sendPrivateMessage(any(PrivateMessageRequestDto.class)))
                .willReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/message/private")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Test message"))
                .andExpect(jsonPath("$.sender.email").value("sender@example.com"));
    }

    @Test
    void getGroupChatMessages_ShouldReturnMessages_WhenValidRequest() throws Exception {
        // Given
        Long groupId = 1L;
        UserDto sender = new UserDto("sender@example.com");
        GetGroupChatMessageDto message = new GetGroupChatMessageDto(sender, "Group message");

        given(groupChatMessageService.getGroupChatMessages(groupId))
                .willReturn(List.of(message));

        // When & Then
        mockMvc.perform(get("/message/group-chat/{id}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Group message"))
                .andExpect(jsonPath("$[0].sender.email").value("sender@example.com"));
    }

    @Test
    void sendGroupChatMessage_ShouldReturnMessage_WhenValidRequest() throws Exception {
        // Given
        GroupChatMessageRequestDto requestDto = new GroupChatMessageRequestDto(1L, "Group message");
        GroupChatMessageResponseDto responseDto = new GroupChatMessageResponseDto("Group 1", "Group message");

        given(groupChatMessageService.sendGroupChatMessage(any(GroupChatMessageRequestDto.class)))
                .willReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/message/group-chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Group message"))
                .andExpect(jsonPath("$.name").value("Group 1"));
    }

    @Test
    void sendCommunityMessage_ShouldReturnMessage_WhenValidRequest() throws Exception {
        // Given
        SendCommunityMessageRequestDto requestDto = new SendCommunityMessageRequestDto(1L, "Community message");
        SendCommunityMessageResponseDto responseDto = new SendCommunityMessageResponseDto("Community 1", "Community message");

        given(communityMessageService.sendCommunityMessage(any(SendCommunityMessageRequestDto.class)))
                .willReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/message/community")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Community message"))
                .andExpect(jsonPath("$.communityName").value("Community 1"));
    }
}


