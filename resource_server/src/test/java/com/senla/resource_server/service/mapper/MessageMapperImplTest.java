package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageMapperImplTest {

    private MessageMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new MessageMapperImpl();
    }

    @Test
    void toPrivateMessageResponse_shouldMapCorrectly() {
        // given
        var sender = User.builder()
                .email("sender@example.com")
                .build();

        var message = PrivateMessage.builder()
                .content("Hello, private message!")
                .sender(sender)
                .build();

        // when
        var dto = mapper.toPrivateMessageResponse(message);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getContent()).isEqualTo("Hello, private message!");
        assertThat(dto.getSender()).isNotNull();
        assertThat(dto.getSender().getEmail()).isEqualTo("sender@example.com");
    }

    @Test
    void toPrivateMessageResponse_shouldReturnNull_whenInputIsNull() {
        // given when then
        assertThat(mapper.toPrivateMessageResponse(null)).isNull();
    }

    @Test
    void userToUserDto_shouldMapCorrectly() throws Exception {
        // given
        var user = User.builder()
                .email("user@example.com")
                .build();

        var method = MessageMapperImpl.class.getDeclaredMethod("userToUserDto", User.class);
        method.setAccessible(true);

        // when
        var dto = (UserDto) method.invoke(mapper, user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void userToUserDto_shouldReturnNull_whenInputIsNull() throws Exception {
        // given
        var method = MessageMapperImpl.class.getDeclaredMethod("userToUserDto", User.class);
        method.setAccessible(true);

        // when
        var dto = (UserDto) method.invoke(mapper, new Object[]{null});

        // then
        assertThat(dto).isNull();
    }
}


