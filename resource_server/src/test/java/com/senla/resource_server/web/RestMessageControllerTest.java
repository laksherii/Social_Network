package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.service.interfaces.PublicMessageService;
import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import com.senla.resource_server.service.interfaces.PrivateMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestMessageController.class)
@Import(TestConfig.class)
class RestMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PublicMessageService publicMessageService;

    @MockitoBean
    private PrivateMessageService privateMessageService;

    private UserDto createUserDto(String email) {
        return UserDto.builder().email(email).build();
    }

    private PrivateMessageResponseDto createResponseDto() {
        return PrivateMessageResponseDto.builder()
                .sender(createUserDto("sender@example.com"))
                .content("Test message content")
                .build();
    }

    private PrivateMessageRequestDto createRequestDto() {
        return PrivateMessageRequestDto.builder()
                .recipientEmail("recipient@example.com")
                .message("Test message")
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMessagesByRecipientEmail_ValidEmail_ReturnsOk() throws Exception {
        // Given
        String validEmail = "test@example.com";
        List<PrivateMessageResponseDto> messages = Collections.singletonList(createResponseDto());

        given(privateMessageService.getMessagesByRecipientEmail(validEmail))
                .willReturn(messages);

        // When
        var result = mockMvc.perform(get("/message/private")
                .param("email", validEmail)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Test message content"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void sendPrivateMessage_ValidRequest_ReturnsOk() throws Exception {
        // Given
        PrivateMessageRequestDto request = createRequestDto();
        PrivateMessageResponseDto response = createResponseDto();

        given(privateMessageService.sendPrivateMessage(any(PrivateMessageRequestDto.class)))
                .willReturn(response);

        // When
        var result = mockMvc.perform(post("/message/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test message content"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void sendPrivateMessage_BlankContent_ReturnsBadRequest() throws Exception {
        // Given
        PrivateMessageRequestDto invalidRequest = PrivateMessageRequestDto.builder()
                .recipientEmail("valid@email.com")
                .message("")
                .build();

        // When
        var result = mockMvc.perform(post("/message/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void sendPrivateMessage_InvalidRecipient_ReturnsBadRequest() throws Exception {
        // Given
        PrivateMessageRequestDto invalidRequest = PrivateMessageRequestDto.builder()
                .recipientEmail("invalid-email")
                .message("Valid message")
                .build();

        // When
        var result = mockMvc.perform(post("/message/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void sendCommunityMessage_ShouldReturnOk() throws Exception {
        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(1L)
                .message("Hello, community!")
                .build();

        SendCommunityMessageResponseDto responseDto = SendCommunityMessageResponseDto.builder()
                .communityName("Community One")
                .content("Hello, community!")
                .build();

        when(publicMessageService.sendCommunityMessage(any())).thenReturn(responseDto);

        mockMvc.perform(post("/message/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.communityName").value("Community One"))
                .andExpect(jsonPath("$.content").value("Hello, community!"));

        verify(publicMessageService).sendCommunityMessage(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void sendGroupChatMessage_ShouldReturnOk() throws Exception {
        GroupChatMessageRequestDto requestDto = GroupChatMessageRequestDto.builder()
                .groupId(1L)
                .content("Hello group!")
                .build();

        GroupChatMessageResponseDto responseDto = GroupChatMessageResponseDto.builder()
                .name("Group 1")
                .content("Hello group!")
                .build();

        when(publicMessageService.sendGroupChatMessage(any())).thenReturn(responseDto);

        mockMvc.perform(post("/message/group-chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Group 1"))
                .andExpect(jsonPath("$.content").value("Hello group!"));

        verify(publicMessageService).sendGroupChatMessage(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void sendWallMessage_ShouldReturnOk() throws Exception {
        WallRequestDto requestDto = WallRequestDto.builder()
                .message("Wall post")
                .build();

        WallResponseDto responseDto = WallResponseDto.builder()
                .owner(UserDto.builder().email("user@example.com").build())
                .content("Wall post")
                .build();

        when(publicMessageService.sendWallMessage(any())).thenReturn(responseDto);

        mockMvc.perform(post("/message/wall")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.email").value("user@example.com"))
                .andExpect(jsonPath("$.content").value("Wall post"));

        verify(publicMessageService).sendWallMessage(any());
    }

    @Test
    @WithMockUser
    void sendCommunityMessage_whenContentBlank_shouldReturnBadRequest() throws Exception {
        SendCommunityMessageRequestDto requestDto = SendCommunityMessageRequestDto.builder()
                .communityId(1L)
                .message(" ")
                .build();

        mockMvc.perform(post("/message/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void sendGroupChatMessage_whenGroupIdNull_shouldReturnBadRequest() throws Exception {
        GroupChatMessageRequestDto requestDto = GroupChatMessageRequestDto.builder()
                .groupId(null)
                .content("Hello group")
                .build();

        mockMvc.perform(post("/message/group-chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void sendGroupChatMessage_whenContentBlank_shouldReturnBadRequest() throws Exception {
        GroupChatMessageRequestDto requestDto = GroupChatMessageRequestDto.builder()
                .groupId(10L)
                .content(" ")
                .build();

        mockMvc.perform(post("/message/group-chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void sendWallMessage_whenMessageTooLong_shouldReturnBadRequest() throws Exception {
        String longMessage = "A".repeat(1001); // invalid: > 1000
        WallRequestDto requestDto = WallRequestDto.builder()
                .message(longMessage)
                .build();

        mockMvc.perform(post("/message/wall")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}