//package com.senla.resource_server.service.impl;
//
//import com.senla.resource_server.data.dao.GroupChatDao;
//import com.senla.resource_server.data.dao.GroupChatMessageDao;
//import com.senla.resource_server.data.dao.UserDao;
//import com.senla.resource_server.data.entity.GroupChat;
//import com.senla.resource_server.data.entity.GroupChatMessage;
//import com.senla.resource_server.data.entity.User;
//import com.senla.resource_server.exception.EntityNotFoundException;
//import com.senla.resource_server.exception.UserNotInGroupChatException;
//import com.senla.resource_server.service.dto.message.GetGroupChatMessageDto;
//import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
//import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
//import com.senla.resource_server.service.mapper.MessageMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class GroupChatMessageServiceImplTest {
//
//    @Mock
//    private GroupChatMessageDao groupChatMessageDao;
//
//    @Mock
//    private GroupChatDao groupChatDao;
//
//    @Mock
//    private UserDao userDaoImpl;
//
//    @Mock
//    private MessageMapper messageMapper;
//
//    @InjectMocks
//    private GroupChatMessageServiceImpl service;
//
//    private User user;
//    private GroupChat groupChat;
//    private GroupChatMessage message;
//
//    private void mockAuthentication(String userEmail) {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn(userEmail);
//        SecurityContext context = mock(SecurityContext.class);
//        when(context.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(context);
//    }
//
//    private final Long groupId = 1L;
//    private final String userEmail = "test@example.com";
//
//    @BeforeEach
//    void setup() {
//        user = new User();
//        user.setEmail("user@example.com");
//
//        groupChat = new GroupChat();
//        groupChat.setId(groupId);
//        groupChat.setUsers(new HashSet<>(List.of(user)));
//        groupChat.setMessages(new ArrayList<>());
//
//        message = new GroupChatMessage();
//        message.setId(10L);
//        message.setMessage("Hello!");
//        message.setSender(user);
//        message.setGroupChat(groupChat);
//
//    }
//
//    @Test
//    void shouldSendGroupChatMessageSuccessfully() {
//        // given
//        GroupChatMessageRequestDto requestDto = new GroupChatMessageRequestDto();
//        requestDto.setMessage("Hello!");
//        requestDto.setGroupId(groupId);
//
//        mockAuthentication(user.getEmail());
//
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.of(groupChat));
//        when(userDaoImpl.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//        when(groupChatMessageDao.save(any(GroupChatMessage.class))).thenReturn(message);
//        when(messageMapper.toGroupChatMessageResponse(any())).thenReturn(new GroupChatMessageResponseDto());
//
//        // when
//        GroupChatMessageResponseDto response = service.sendGroupChatMessage(requestDto);
//
//        // then
//        assertThat(response).isNotNull();
//        verify(groupChatMessageDao).save(any(GroupChatMessage.class));
//    }
//
//    @Test
//    void shouldThrowException_whenUserNotInGroupChat_onSend() {
//        // given
//        GroupChatMessageRequestDto requestDto = new GroupChatMessageRequestDto();
//        requestDto.setMessage("Hi");
//        requestDto.setGroupId(groupId);
//        groupChat.setUsers(new HashSet<>());
//
//        mockAuthentication(user.getId());
//
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.of(groupChat));
//        when(userDaoImpl.findById(userId)).thenReturn(Optional.of(user));
//
//        // when & then
//        assertThatThrownBy(() -> service.sendGroupChatMessage(requestDto))
//                .isInstanceOf(UserNotInGroupChatException.class);
//    }
//
//    @Test
//    void shouldGetGroupChatMessagesSuccessfully() {
//        // given
//        GroupChatMessage msg = new GroupChatMessage();
//        msg.setMessage("Message");
//        msg.setSender(user);
//        msg.setGroupChat(groupChat);
//        groupChat.getMessages().add(msg);
//
//        mockAuthentication(user.getId());
//
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.of(groupChat));
//        when(userDaoImpl.findById(userId)).thenReturn(Optional.of(user));
//        when(messageMapper.toGetGroupChatMessage(msg)).thenReturn(new GetGroupChatMessageDto());
//
//        // when
//        List<GetGroupChatMessageDto> messages = service.getGroupChatMessages(groupId);
//
//        // then
//        assertThat(messages).hasSize(1);
//    }
//
//    @Test
//    void shouldThrowException_whenUserNotInGroupChat_onGetMessages() {
//        // given
//        groupChat.setUsers(new HashSet<>()); // user not in chat
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.of(groupChat));
//        when(userDaoImpl.findById(userId)).thenReturn(Optional.of(user));
//
//        mockAuthentication(user.getId());
//
//        // when & then
//        assertThatThrownBy(() -> service.getGroupChatMessages(groupId))
//                .isInstanceOf(UserNotInGroupChatException.class);
//    }
//
//    @Test
//    void shouldThrowException_whenGroupChatNotFound_onSend() {
//        // given
//        GroupChatMessageRequestDto requestDto = new GroupChatMessageRequestDto();
//        requestDto.setGroupId(groupId);
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> service.sendGroupChatMessage(requestDto))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void shouldThrowException_whenGroupChatNotFound_onGetMessages() {
//        // given
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.empty());
//        // when & then
//        assertThatThrownBy(() -> service.getGroupChatMessages(groupId))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void shouldThrowException_whenUserNotFound_onSend() {
//        // given
//        GroupChatMessageRequestDto requestDto = new GroupChatMessageRequestDto();
//        requestDto.setGroupId(groupId);
//
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.of(groupChat));
//        when(userDaoImpl.findById(userId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> service.sendGroupChatMessage(requestDto))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void shouldThrowException_whenUserNotFound_onGetMessages() {
//        // given
//        when(groupChatDao.findById(groupId)).thenReturn(Optional.of(groupChat));
//        when(userDaoImpl.findById(userId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> service.getGroupChatMessages(groupId))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//}
//
