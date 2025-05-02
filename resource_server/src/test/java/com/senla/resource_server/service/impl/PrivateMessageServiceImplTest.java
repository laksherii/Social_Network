package com.senla.resource_server.service.impl;

import com.senla.resource_server.config.UserIdAuthenticationToken;
import com.senla.resource_server.data.dao.PrivateMessageDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateMessageServiceImplTest {

    @Mock
    private PrivateMessageDao privateMessageDao;

    @Mock
    private UserDao userDao;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private PrivateMessageServiceImpl service;

    private final Long senderId = 1L;
    private final Long recipientId = 2L;

    private User sender;
    private User recipient;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(senderId);
        sender.setEmail("sender@example.com");

        recipient = new User();
        recipient.setId(recipientId);
        recipient.setEmail("recipient@example.com");

        UserIdAuthenticationToken authentication = mock(UserIdAuthenticationToken.class);
        when(authentication.getUserId()).thenReturn(senderId);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void shouldSendPrivateMessageSuccessfully() {
        // given
        PrivateMessageRequestDto requestDto = new PrivateMessageRequestDto();
        requestDto.setRecipient(recipientId);
        requestDto.setMessage("Hello!");

        PrivateMessage message = new PrivateMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setMessage("Hello!");

        PrivateMessage savedMessage = new PrivateMessage();
        savedMessage.setId(42L);
        savedMessage.setSender(sender);
        savedMessage.setRecipient(recipient);
        savedMessage.setMessage("Hello!");

        PrivateMessageResponseDto expectedResponse = new PrivateMessageResponseDto();
        UserDto userDto = new UserDto();
        expectedResponse.setSender(userDto);
        expectedResponse.setMessage("Hello!");

        when(userDao.findById(senderId)).thenReturn(Optional.of(sender));
        when(userDao.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(privateMessageDao.save(any())).thenReturn(savedMessage);
        when(messageMapper.toPrivateMessageResponse(savedMessage)).thenReturn(expectedResponse);

        // when
        PrivateMessageResponseDto response = service.sendPrivateMessage(requestDto);

        // then
        assertThat(response.getSender()).isEqualTo(expectedResponse.getSender());
        assertThat(response.getMessage()).isEqualTo(expectedResponse.getMessage());

        verify(privateMessageDao).save(any(PrivateMessage.class));
        verify(messageMapper).toPrivateMessageResponse(savedMessage);
    }

    @Test
    void shouldGetMessagesByRecipientEmailSuccessfully() {
        // given
        String recipientEmail = "recipient@example.com";

        PrivateMessage message = new PrivateMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setMessage("Hi!");

        List<PrivateMessage> messages = List.of(message);

        PrivateMessageResponseDto expectedResponse = new PrivateMessageResponseDto();
        UserDto userDto = new UserDto();
        expectedResponse.setSender(userDto);
        expectedResponse.setMessage("Hello!");

        when(userDao.findById(senderId)).thenReturn(Optional.of(sender));
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.of(recipient));
        when(privateMessageDao.findBySenderAndRecipient(sender, recipient)).thenReturn(messages);
        when(messageMapper.toPrivateMessageResponse(message)).thenReturn(expectedResponse);

        // when
        List<PrivateMessageResponseDto> result = service.getMessagesByRecipientEmail(recipientEmail);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSender()).isEqualTo(expectedResponse.getSender());
        assertThat(result.get(0).getMessage()).isEqualTo(expectedResponse.getMessage());

        verify(privateMessageDao).findBySenderAndRecipient(sender, recipient);
    }

    @Test
    void shouldThrowException_whenSenderNotFound_onSend() {
        // given
        PrivateMessageRequestDto requestDto = new PrivateMessageRequestDto();
        requestDto.setRecipient(recipientId);
        requestDto.setMessage("Hello!");

        when(userDao.findById(senderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.sendPrivateMessage(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldThrowException_whenRecipientNotFound_onSend() {
        // given
        PrivateMessageRequestDto requestDto = new PrivateMessageRequestDto();
        requestDto.setRecipient(recipientId);
        requestDto.setMessage("Hello!");

        when(userDao.findById(senderId)).thenReturn(Optional.of(sender));
        when(userDao.findById(recipientId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.sendPrivateMessage(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowException_whenSenderNotFound_onGetMessages() {
        // given
        String recipientEmail = "recipient@example.com";

        when(userDao.findById(senderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.getMessagesByRecipientEmail(recipientEmail))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowException_whenRecipientNotFound_onGetMessages() {
        // given
        String recipientEmail = "recipient@example.com";

        when(userDao.findById(senderId)).thenReturn(Optional.of(sender));
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.getMessagesByRecipientEmail(recipientEmail))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldReturnEmptyList_whenNoMessagesFound() {
        // given
        String recipientEmail = "recipient@example.com";

        List<PrivateMessage> messages = new ArrayList<>();
        when(userDao.findById(senderId)).thenReturn(Optional.of(sender));
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.of(recipient));
        when(privateMessageDao.findBySenderAndRecipient(sender, recipient)).thenReturn(messages);

        // when
        List<PrivateMessageResponseDto> result = service.getMessagesByRecipientEmail(recipientEmail);

        // then
        assertThat(result).isEmpty();
    }
}
