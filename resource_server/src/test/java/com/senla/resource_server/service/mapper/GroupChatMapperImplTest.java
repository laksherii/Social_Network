package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GroupChatMapperImplTest {

    private GroupChatMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new GroupChatMapperImpl();
    }

    @Test
    void toCreateGroupDtoResponse_shouldReturnNull_whenInputIsNull() {
        // given
        GroupChat chat = null;

        // when
        CreateGroupChatResponseDto dto = mapper.toCreateGroupDtoResponse(chat);

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
        CreateGroupChatResponseDto dto = mapper.toCreateGroupDtoResponse(chat);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Chat");
        assertThat(dto.getUsers()).hasSize(1);
        assertThat(dto.getUsers().iterator().next().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void toGroupChatMessageResponseDto_shouldReturnNull_whenInputIsNull() {
        // given
        PublicMessage message = null;

        // when
        GroupChatMessageResponseDto dto = mapper.toGroupChatMessageResponseDto(message);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void toGroupChatMessageResponseDto_shouldMapFieldsCorrectly_whenInputIsNotNull() {
        // given
        GroupChat chat = GroupChat.builder()
                .id(1L)
                .name("Group 1")
                .build();
        PublicMessage message = PublicMessage.builder()
                .groupChat(chat)
                .content("Hello Group")
                .build();

        // when
        GroupChatMessageResponseDto dto = mapper.toGroupChatMessageResponseDto(message);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Group 1");
        assertThat(dto.getContent()).isEqualTo("Hello Group");
    }

    @Test
    void toGroupChatDto_shouldReturnNull_whenInputIsNull() {
        // given
        GroupChat groupChat = null;

        // when
        GroupChatDto dto = mapper.toGroupChatDto(groupChat);

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
        GroupChatDto dto = mapper.toGroupChatDto(groupChat);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Chat Room");
        assertThat(dto.getMessages()).hasSize(1);

        GroupChatMessageDto messageDto = dto.getMessages().get(0);
        assertThat(messageDto.getSender()).isNotNull();
        assertThat(messageDto.getSender().getEmail()).isEqualTo("sender@example.com");
        assertThat(messageDto.getContent()).isEqualTo("Hi!");
    }

    @Test
    void userToUserDto_shouldReturnNull_whenInputIsNull() {
        // given
        User user = null;

        // when
        UserDto dto = mapper.userToUserDto(user);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void userToUserDto_shouldMapFieldsCorrectly_whenInputIsNotNull() {
        // given
        User user = User.builder().email("test@example.com").build();

        // when
        UserDto dto = mapper.userToUserDto(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userSetToUserDtoSet_shouldReturnNull_whenInputIsNull() {
        // given
        Set<User> users = null;

        // when
        Set<UserDto> dtos = mapper.userSetToUserDtoSet(users);

        // then
        assertThat(dtos).isNull();
    }

    @Test
    void userSetToUserDtoSet_shouldMapFieldsCorrectly_whenInputIsNotNull() {
        // given
        User user = User.builder().email("user@example.com").build();
        Set<User> users = Set.of(user);

        // when
        Set<UserDto> dtos = mapper.userSetToUserDtoSet(users);

        // then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.iterator().next().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void publicMessageToGroupChatMessageDto_shouldReturnNull_whenInputIsNull() {
        // given
        PublicMessage msg = null;

        // when
        GroupChatMessageDto dto = mapper.publicMessageToGroupChatMessageDto(msg);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void publicMessageToGroupChatMessageDto_shouldMapFieldsCorrectly_whenInputIsNotNull() {
        // given
        User sender = User.builder().email("sender@example.com").build();
        PublicMessage msg = PublicMessage.builder()
                .sender(sender)
                .content("Test message")
                .build();

        // when
        GroupChatMessageDto dto = mapper.publicMessageToGroupChatMessageDto(msg);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getSender()).isNotNull();
        assertThat(dto.getSender().getEmail()).isEqualTo("sender@example.com");
        assertThat(dto.getContent()).isEqualTo("Test message");
    }

    @Test
    void publicMessageListToGroupChatMessageDtoList_shouldReturnNull_whenInputIsNull() {
        // given
        List<PublicMessage> messages = null;

        // when
        List<GroupChatMessageDto> dtos = mapper.publicMessageListToGroupChatMessageDtoList(messages);

        // then
        assertThat(dtos).isNull();
    }

    @Test
    void publicMessageListToGroupChatMessageDtoList_shouldMapFieldsCorrectly_whenInputIsNotNull() {
        // given
        User sender = User.builder().email("a@b.com").build();
        PublicMessage msg = PublicMessage.builder()
                .sender(sender)
                .content("message")
                .build();

        List<PublicMessage> messages = List.of(msg);

        // when
        List<GroupChatMessageDto> dtos = mapper.publicMessageListToGroupChatMessageDtoList(messages);

        // then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getContent()).isEqualTo("message");
        assertThat(dtos.get(0).getSender()).isNotNull();
        assertThat(dtos.get(0).getSender().getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void publicMessageGroupChatName_shouldReturnName_whenGroupChatNotNull() throws Exception {
        // given
        GroupChat groupChat = GroupChat.builder().name("ChatName").build();
        PublicMessage publicMessage = PublicMessage.builder().groupChat(groupChat).build();

        Method method = GroupChatMapperImpl.class.getDeclaredMethod("publicMessageGroupChatName", PublicMessage.class);
        method.setAccessible(true);

        // when
        String result = (String) method.invoke(mapper, publicMessage);

        // then
        assertThat(result).isEqualTo("ChatName");
    }

    @Test
    void publicMessageGroupChatName_shouldReturnNull_whenGroupChatIsNull() throws Exception {
        // given
        PublicMessage publicMessage = PublicMessage.builder().groupChat(null).build();

        Method method = GroupChatMapperImpl.class.getDeclaredMethod("publicMessageGroupChatName", PublicMessage.class);
        method.setAccessible(true);

        // when
        String result = (String) method.invoke(mapper, publicMessage);

        // then
        assertThat(result).isNull();
    }
}
