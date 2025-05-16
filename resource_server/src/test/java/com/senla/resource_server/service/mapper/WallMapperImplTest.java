package com.senla.resource_server.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.service.dto.message.WallMessageDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WallMapperImplTest {

    private WallMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new WallMapperImpl();
    }

    @Test
    void toWallResponseDto_shouldReturnNull_whenWallIsNull() {
        // given
        Wall input = null;

        // when
        WallResponseDto result = mapper.toWallResponseDto(input);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toWallMessageDto_shouldReturnNull_whenPublicMessageIsNull() {
        // given
        PublicMessage input = null;

        // when
        WallMessageDto result = mapper.toWallMessageDto(input);

        // then
        assertThat(result).isNull();
    }

    @Test
    void userToUserDto_shouldReturnNull_whenUserIsNull() throws Exception {
        // given
        Method method = WallMapperImpl.class.getDeclaredMethod("userToUserDto", User.class);
        method.setAccessible(true);

        // when
        Object result = method.invoke(mapper, (Object) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void publicMessageListToWallMessageDtoList_shouldReturnNull_whenListIsNull() throws Exception {
        // given
        Method method = WallMapperImpl.class.getDeclaredMethod("publicMessageListToWallMessageDtoList", List.class);
        method.setAccessible(true);

        // when
        Object result = method.invoke(mapper, (Object) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toWallResponseDto_shouldMapWallWithOwnerAndMessages_correctly() {
        // given
        User owner = User.builder()
                .email("owner@example.com")
                .build();

        PublicMessage msg1 = new PublicMessage();
        msg1.setContent("Hello");
        PublicMessage msg2 = new PublicMessage();
        msg2.setContent("World");

        List<PublicMessage> messages = new ArrayList<>();
        messages.add(msg1);
        messages.add(msg2);

        Wall wall = Wall.builder()
                .owner(owner)
                .messages(messages)
                .build();

        // when
        WallResponseDto dto = mapper.toWallResponseDto(wall);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getOwner()).isNotNull();
        assertThat(dto.getOwner().getEmail()).isEqualTo(owner.getEmail());
        assertThat(dto.getMessages()).hasSize(2);
        assertThat(dto.getMessages()).extracting("content").containsExactly("Hello", "World");
    }

    @Test
    void toWallMessageDto_shouldMapPublicMessageToDto_correctly() {
        // given
        PublicMessage msg = new PublicMessage();
        msg.setContent("Test message");

        // when
        WallMessageDto dto = mapper.toWallMessageDto(msg);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getContent()).isEqualTo("Test message");
    }

    @Test
    void userToUserDto_shouldMapUserToUserDto_correctly() throws Exception {
        // given
        User user = User.builder()
                .email("testuser@example.com")
                .build();

        Method method = WallMapperImpl.class.getDeclaredMethod("userToUserDto", User.class);
        method.setAccessible(true);

        // when
        UserDto dto = (UserDto) method.invoke(mapper, user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void publicMessageListToWallMessageDtoList_shouldMapListOfPublicMessagesToDtoList_correctly() throws Exception {
        // given
        PublicMessage pm1 = new PublicMessage();
        pm1.setContent("Msg1");
        PublicMessage pm2 = new PublicMessage();
        pm2.setContent("Msg2");

        List<PublicMessage> list = List.of(pm1, pm2);

        Method method = WallMapperImpl.class.getDeclaredMethod("publicMessageListToWallMessageDtoList", List.class);
        method.setAccessible(true);

        // when
        @SuppressWarnings("unchecked")
        List<WallMessageDto> result = (List<WallMessageDto>) method.invoke(mapper, list);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting("content").containsExactly("Msg1", "Msg2");
    }
}
