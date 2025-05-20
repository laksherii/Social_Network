package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageDto;
import com.senla.resource_server.service.dto.user.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GroupChatMapperImplTest {

    private final GroupChatMapper groupChatMapper = Mappers.getMapper(GroupChatMapper.class);

    @Test
    void toCreateGroupDtoResponse_shouldReturnNull_whenInputIsNull() {
        // given
        GroupChat chat = null;

        // when
        CreateGroupChatResponseDto dto = groupChatMapper.toCreateGroupDtoResponse(chat);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void toCreateGroupDtoResponse_shouldMapFieldsCorrectly_whenInputIsNotNull() {
        // given
        User user = User.builder().email("user@example.com").build();
        Set<User> users = Set.of(user);
        GroupChat chat = GroupChat.builder()
                .id(1L)
                .name("Test Chat")
                .users(users)
                .build();

        // when
        CreateGroupChatResponseDto dto = groupChatMapper.toCreateGroupDtoResponse(chat);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Chat");
        assertThat(dto.getUsers()).hasSize(1);
        assertThat(dto.getUsers().iterator().next().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void toGroupChatDto_shouldReturnNull_whenInputIsNull() {
        // given
        GroupChat groupChat = null;

        // when
        GroupChatDto dto = groupChatMapper.toGroupChatDto(groupChat);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void toGroupChatDto_shouldMapFieldsCorrectly_whenInputIsNotNull() {
        // given
        User sender = User.builder().email("sender@example.com").build();
        PublicMessage msg = PublicMessage.builder()
                .sender(sender)
                .content("Hi!")
                .build();
        GroupChat groupChat = GroupChat.builder()
                .name("Chat Room")
                .messages(List.of(msg))
                .build();

        // when
        GroupChatDto dto = groupChatMapper.toGroupChatDto(groupChat);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Chat Room");
        assertThat(dto.getMessages()).hasSize(1);

        GroupChatMessageDto messageDto = dto.getMessages().get(0);
        assertThat(messageDto.getSender()).isNotNull();
        assertThat(messageDto.getSender().getEmail()).isEqualTo("sender@example.com");
        assertThat(messageDto.getContent()).isEqualTo("Hi!");
    }

}
