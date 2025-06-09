package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.PrivateMessageDao;
import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.mapper.MessageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

    @Mock
    private AuthService authService;

    @InjectMocks
    private PrivateMessageServiceImpl privateMessageService;

    @Test
    void sendPrivateMessage_shouldSendMessageAndReturnResponse() {
        // given
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";
        String messageContent = "Hello, test message";

        PrivateMessageRequestDto requestDto = PrivateMessageRequestDto.builder()
                .recipientEmail(recipientEmail)
                .message(messageContent)
                .build();

        User sender = User.builder().id(1L).email(senderEmail).build();
        User recipient = User.builder().id(2L).email(recipientEmail).build();

        PrivateMessage savedMessage = PrivateMessage.builder()
                .id(100L)
                .sender(sender)
                .recipient(recipient)
                .content(messageContent)
                .build();

        PrivateMessageResponseDto responseDto = PrivateMessageResponseDto.builder()
                .sender(UserDto.builder().email(senderEmail).build())
                .content(messageContent)
                .build();

        when(authService.getCurrentUser()).thenReturn(sender);
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.of(recipient));
        when(privateMessageDao.save(any(PrivateMessage.class))).thenReturn(savedMessage);
        when(messageMapper.toPrivateMessageResponse(savedMessage)).thenReturn(responseDto);

        // when
        PrivateMessageResponseDto actualResponse = privateMessageService.sendPrivateMessage(requestDto);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getSender().getEmail()).isEqualTo(senderEmail);
        assertThat(actualResponse.getContent()).isEqualTo(messageContent);

        verify(userDao).findByEmail(recipientEmail);
        verify(privateMessageDao).save(any(PrivateMessage.class));
        verify(messageMapper).toPrivateMessageResponse(savedMessage);
    }

    @Test
    void sendPrivateMessage_shouldThrowWhenRecipientNotFound() {
        // given
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";

        PrivateMessageRequestDto requestDto = PrivateMessageRequestDto.builder()
                .recipientEmail(recipientEmail)
                .message("test")
                .build();

        User sender = User.builder().id(1L).email(senderEmail).build();

        when(authService.getCurrentUser()).thenReturn(sender);
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> privateMessageService.sendPrivateMessage(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(privateMessageDao, never()).save(any());
        verify(messageMapper, never()).toPrivateMessageResponse(any());
    }

    @Test
    void getMessagesByRecipientEmail_shouldReturnListOfMessages() {
        // given
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";


        User sender = User.builder().id(1L).email(senderEmail).build();
        User recipient = User.builder().id(2L).email(recipientEmail).build();

        PrivateMessage msg1 = PrivateMessage.builder()
                .id(10L)
                .sender(sender)
                .recipient(recipient)
                .content("Hi 1")
                .build();

        PrivateMessage msg2 = PrivateMessage.builder()
                .id(11L)
                .sender(sender)
                .recipient(recipient)
                .content("Hi 2")
                .build();

        List<PrivateMessage> messages = List.of(msg1, msg2);

        PrivateMessageResponseDto dto1 = PrivateMessageResponseDto.builder()
                .sender(UserDto.builder().email(senderEmail).build())
                .content("Hi 1")
                .build();

        PrivateMessageResponseDto dto2 = PrivateMessageResponseDto.builder()
                .sender(UserDto.builder().email(senderEmail).build())
                .content("Hi 2")
                .build();

        when(authService.getCurrentUser()).thenReturn(sender);
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.of(recipient));
        when(privateMessageDao.findBySenderAndRecipient(sender, recipient)).thenReturn(messages);
        when(messageMapper.toPrivateMessageResponse(msg1)).thenReturn(dto1);
        when(messageMapper.toPrivateMessageResponse(msg2)).thenReturn(dto2);

        // when
        List<PrivateMessageResponseDto> actualMessages = privateMessageService.getMessagesByRecipientEmail(recipientEmail);

        // then
        assertThat(actualMessages).hasSize(2);
        assertThat(actualMessages).extracting(PrivateMessageResponseDto::getContent)
                .containsExactlyInAnyOrder("Hi 1", "Hi 2");
    }

    @Test
    void getMessagesByRecipientEmail_shouldThrowWhenRecipientNotFound() {
        // given
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";

        User sender = User.builder().id(1L).email(senderEmail).build();

        when(authService.getCurrentUser()).thenReturn(sender);
        when(userDao.findByEmail(recipientEmail)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> privateMessageService.getMessagesByRecipientEmail(recipientEmail))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userDao).findByEmail(recipientEmail);
        verify(privateMessageDao, never()).findBySenderAndRecipient(any(), any());
        verify(messageMapper, never()).toPrivateMessageResponse(any());
    }
}

