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
        UserDto sender = UserDto.builder()
                .email("sender@example.com")
                .build();
        PrivateMessageResponseDto message = PrivateMessageResponseDto.builder()
                .sender(sender)
                .message("Hello")
                .build();

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
        PrivateMessageRequestDto requestDto = PrivateMessageRequestDto.builder()
                .recipientEmail("recipient@example.com")
                .message("Test message")
                .build();
        UserDto sender = UserDto.builder()
                .email("sender@example.com")
                .build();
        PrivateMessageResponseDto responseDto = PrivateMessageResponseDto.builder()
                .sender(sender)
                .message("Test message")
                .build();

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
        UserDto sender = UserDto.builder()
                .email("sender@example.com")
                .build();
        GetGroupChatMessageDto message = GetGroupChatMessageDto.builder()
                .sender(sender)
                .message("Group message")
                .build();

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
        GroupChatMessageRequestDto requestDto = GroupChatMessageRequestDto.builder()
                .groupId(1L)
                .message("Group message")
                .build();
        GroupChatMessageResponseDto responseDto = GroupChatMessageResponseDto.builder()
                .name("Group 1")
                .message("Group message")
                .build();

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
        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(1L)
                .message("Community message")
                .build();
        SendCommunityMessageResponseDto responseDto = SendCommunityMessageResponseDto.builder()
                .communityName("Community 1")
                .message("Community message")
                .build();

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

