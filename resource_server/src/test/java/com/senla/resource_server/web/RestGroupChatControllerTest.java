package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.impl.GroupChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestGroupChatController.class)
@WithMockUser
class RestGroupChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupChatServiceImpl groupService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createGroupChat_shouldReturnCreated() throws Exception {
        // Given
        UserDto userDto1 = new UserDto();
        userDto1.setEmail("user1@email.com");
        UserDto userDto2 = new UserDto();
        userDto2.setEmail("user2@email.com");
        UserDto userDto3 = new UserDto();
        userDto3.setEmail("user3@email.com");

        CreateGroupChatRequestDto requestDto = new CreateGroupChatRequestDto("Fun Chat", Set.of(1L, 2L, 3L));
        CreateGroupChatResponseDto responseDto = new CreateGroupChatResponseDto(1L, "Fun Chat",
                Set.of(userDto1, userDto2, userDto3));

        given(groupService.create(requestDto)).willReturn(responseDto);

        // When / Then
        mockMvc.perform(post("/group-chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fun Chat"))
                .andReturn().getResponse().getContentAsString();

    }
}