package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.service.dto.message.WallMessageDto;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperImplTest {

    private UserMapperImpl userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toUserDto_shouldMapFields() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .build();

        // when
        UserDto result = userMapper.toUserDto(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUserDto_shouldReturnNull_whenUserIsNull() {
        // given when then
        assertThat(userMapper.toUserDto(null)).isNull();
    }

    @Test
    void toUserInfoDto_shouldMapFieldsIncludingWall() {
        // given
        User user = User.builder()
                .email("info@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.of(1990, 1, 1))
                .wall(Wall.builder()
                        .messages(Collections.singletonList(
                                PublicMessage.builder().content("Hello wall!").build()
                        ))
                        .build())
                .build();

        // when
        UserInfoDto dto = userMapper.toUserInfoDto(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("info@example.com");
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getBirthDay()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(dto.getWall()).isNotNull();
        assertThat(dto.getWall().getMessages()).hasSize(1);
        assertThat(dto.getWall().getMessages().get(0).getContent()).isEqualTo("Hello wall!");
    }

    @Test
    void toUserInfoDto_shouldReturnNull_whenUserIsNull() {
        // given when then
        assertThat(userMapper.toUserInfoDto(null)).isNull();
    }

    @Test
    void toUserCreate_shouldMapFields() {
        // given
        CreateUserDtoRequest req = CreateUserDtoRequest.builder()
                .email("create@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .birthDay(LocalDate.of(1985, 5, 5))
                .gender(User.GenderType.FEMALE)
                .build();

        // when
        User user = userMapper.toUserCreate(req);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("create@example.com");
        assertThat(user.getFirstName()).isEqualTo("Alice");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getBirthDay()).isEqualTo(LocalDate.of(1985, 5, 5));
        assertThat(user.getGender()).isEqualTo(User.GenderType.FEMALE);
    }

    @Test
    void toUserCreate_shouldReturnNull_whenRequestIsNull() {
        // given when then
        assertThat(userMapper.toUserCreate(null)).isNull();
    }

    @Test
    void toUserSearchDtoResponse_shouldMapFields() {
        // given
        User user = User.builder()
                .email("search@example.com")
                .firstName("Bob")
                .lastName("Brown")
                .birthDay(LocalDate.of(2000, 12, 12))
                .gender(User.GenderType.MALE)
                .build();

        // when
        UserSearchDtoResponse dto = userMapper.toUserSearchDtoResponse(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("search@example.com");
        assertThat(dto.getFirstName()).isEqualTo("Bob");
        assertThat(dto.getLastName()).isEqualTo("Brown");
        assertThat(dto.getBirthDay()).isEqualTo(LocalDate.of(2000, 12, 12));
        assertThat(dto.getGender()).isEqualTo(User.GenderType.MALE);
    }

    @Test
    void toUserSearchDtoResponse_shouldReturnNull_whenUserIsNull() {
        // given when then
        assertThat(userMapper.toUserSearchDtoResponse(null)).isNull();
    }

    @Test
    void toCreateUserDtoResponse_shouldMapFieldsIncludingWall() {
        // given
        Wall wall = Wall.builder()
                .owner(User.builder().email("owner@example.com").build())
                .messages(Collections.singletonList(PublicMessage.builder().content("Wall msg").build()))
                .build();

        User user = User.builder()
                .email("response@example.com")
                .firstName("Eve")
                .lastName("White")
                .birthDay(LocalDate.of(1995, 3, 15))
                .wall(wall)
                .build();

        // when
        CreateUserDtoResponse dto = userMapper.toCreateUserDtoResponse(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("response@example.com");
        assertThat(dto.getFirstName()).isEqualTo("Eve");
        assertThat(dto.getLastName()).isEqualTo("White");
        assertThat(dto.getBirthDay()).isEqualTo(LocalDate.of(1995, 3, 15));
        assertThat(dto.getWall()).isNotNull();
        assertThat(dto.getWall().getOwner()).isNotNull();
        assertThat(dto.getWall().getOwner().getEmail()).isEqualTo("owner@example.com");
        assertThat(dto.getWall().getMessages()).hasSize(1);
        assertThat(dto.getWall().getMessages().get(0).getContent()).isEqualTo("Wall msg");
    }

    @Test
    void toCreateUserDtoResponse_shouldReturnNull_whenUserIsNull() {
        // given when then
        assertThat(userMapper.toCreateUserDtoResponse(null)).isNull();
    }

    @Test
    void toUpdateRoleUserDtoResponse_shouldMapFields() {
        // given
        User user = User.builder()
                .email("role@example.com")
                .role(User.RoleType.ROLE_ADMIN)
                .build();

        // when
        UpdateRoleUserDtoResponse dto = userMapper.toUpdateRoleUserDtoResponse(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("role@example.com");
        assertThat(dto.getRole()).isEqualTo(User.RoleType.ROLE_ADMIN);
    }

    @Test
    void toUpdateRoleUserDtoResponse_shouldReturnNull_whenUserIsNull() {
        // given when then
        assertThat(userMapper.toUpdateRoleUserDtoResponse(null)).isNull();
    }

    @Test
    void wallToWallDto_shouldReturnNull_whenWallIsNull() throws Exception {
        // given
        var method = UserMapperImpl.class.getDeclaredMethod("wallToWallDto", Wall.class);
        method.setAccessible(true);

        // when
        Object result = method.invoke(userMapper, (Object) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void publicMessageToWallMessageDto_shouldReturnNull_whenInputIsNull() {
        // given when then
        WallMessageDto result = userMapper.publicMessageToWallMessageDto(null);
        assertThat(result).isNull();
    }

    @Test
    void publicMessageListToWallMessageDtoList_shouldReturnNull_whenInputListIsNull() {
        // given when then
        List<WallMessageDto> result = userMapper.publicMessageListToWallMessageDtoList(null);
        assertThat(result).isNull();
    }

    @Test
    void wallToWallResponseDto_shouldReturnNull_whenWallIsNull() throws Exception {
        // given
        var method = UserMapperImpl.class.getDeclaredMethod("wallToWallResponseDto", Wall.class);
        method.setAccessible(true);

        // when
        Object result = method.invoke(userMapper, (Object) null);

        // then
        assertThat(result).isNull();
    }
}
