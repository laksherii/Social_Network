package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.dao.WallDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.data.entity.WallMessage;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.message.WallMessageDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import com.senla.resource_server.service.mapper.WallMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WallServiceImplTest {

    @Mock
    private WallDao wallDao;

    @Mock
    private UserDao userDao;

    @Mock
    private WallMapper wallMapper;

    @InjectMocks
    private WallServiceImpl wallService;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void updateWall_WhenCalled_ShouldAddMessageAndReturnDto() {
        // given
        WallRequestDto requestDto = WallRequestDto.builder()
                .message("Hello, world!")
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .wall(new Wall())
                .build();

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        WallMessage newMessage = new WallMessage();
        newMessage.setMessage("Hello, world!");
        newMessage.setSender(user);

        Wall wallWithMessage = new Wall();
        wallWithMessage.setMessages(List.of(newMessage));
        wallWithMessage.setOwner(user);

        WallMessageDto wallMessageDto = new WallMessageDto();
        wallMessageDto.setMessage("Hello, world!");

        Wall updatedWall = new Wall();
        updatedWall.setMessages(List.of(newMessage));
        updatedWall.setOwner(user);

        WallResponseDto expectedResponse = WallResponseDto.builder()
                .owner(userDto)
                .messages(List.of(wallMessageDto))
                .build();

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(wallDao.update(any(Wall.class))).thenReturn(updatedWall);
        when(wallMapper.toWallResponseDto(updatedWall)).thenReturn(expectedResponse);

        // when
        WallResponseDto result = wallService.updateWall(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOwner()).isEqualTo(expectedResponse.getOwner());
        assertThat(result.getMessages()).isEqualTo(expectedResponse.getMessages());
    }

    @Test
    void updateWall_WhenUserNotFound_ShouldThrowException() {
        // given
        WallRequestDto requestDto = WallRequestDto.builder()
                .message("Hi")
                .build();

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> wallService.updateWall(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }
}

