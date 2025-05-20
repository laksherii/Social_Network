//package com.senla.resource_server.service.mapper;
//
//import com.senla.resource_server.data.entity.Community;
//import com.senla.resource_server.data.entity.GroupChat;
//import com.senla.resource_server.data.entity.PrivateMessage;
//import com.senla.resource_server.data.entity.PublicMessage;
//import com.senla.resource_server.data.entity.User;
//import com.senla.resource_server.data.entity.Wall;
//import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
//import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
//import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
//import com.senla.resource_server.service.dto.user.UserDto;
//import com.senla.resource_server.service.dto.wall.WallResponseDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mapstruct.factory.Mappers;
//
//import java.lang.reflect.Method;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class MessageMapperImplTest {
//
//    private final MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);
//
//    @Test
//    void toPrivateMessageResponse_NullMessage_ReturnsNull() {
//        assertThat(messageMapper.toPrivateMessageResponse(null)).isNull();
//    }
//
//    @Test
//    void toPrivateMessageResponse_ValidMessage_ReturnsDto() {
//        User sender = User.builder()
//                .email("sender@example.com")
//                .build();
//
//        PrivateMessage message = PrivateMessage.builder()
//                .content("hello")
//                .sender(sender)
//                .build();
//
//        PrivateMessageResponseDto dto = messageMapper.toPrivateMessageResponse(message);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getContent()).isEqualTo("hello");
//        assertThat(dto.getSender()).isNotNull();
//        assertThat(dto.getSender().getEmail()).isEqualTo("sender@example.com");
//    }
//
//    @Test
//    void toCommunityMessageResponse_NullMessage_ReturnsNull() {
//        assertThat(messageMapper.toCommunityMessageResponse(null)).isNull();
//    }
//
//    @Test
//    void toCommunityMessageResponse_ValidMessage_ReturnsDto() {
//        Community community = Community.builder()
//                .name("TestCommunity")
//                .build();
//
//        PublicMessage message = PublicMessage.builder()
//                .content("Message to community")
//                .community(community)
//                .build();
//
//        SendCommunityMessageResponseDto dto = messageMapper.toCommunityMessageResponse(message);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getContent()).isEqualTo("Message to community");
//        assertThat(dto.getCommunityName()).isEqualTo("TestCommunity");
//    }
//
//    @Test
//    void toGroupChatMessageResponse_NullMessage_ReturnsNull() {
//        assertThat(messageMapper.toGroupChatMessageResponse(null)).isNull();
//    }
//
//    @Test
//    void toGroupChatMessageResponse_ValidMessage_ReturnsDto() {
//        GroupChat chat = GroupChat.builder()
//                .name("TestGroup")
//                .build();
//
//        PublicMessage message = PublicMessage.builder()
//                .content("Group message")
//                .groupChat(chat)
//                .build();
//
//        GroupChatMessageResponseDto dto = messageMapper.toGroupChatMessageResponse(message);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getContent()).isEqualTo("Group message");
//        assertThat(dto.getName()).isEqualTo("TestGroup");
//    }
//
//    @Test
//    void toWallMessageResponse_NullMessage_ReturnsNull() {
//        assertThat(messageMapper.toWallMessageResponse(null)).isNull();
//    }
//
//    @Test
//    void toWallMessageResponse_ValidMessage_ReturnsDto() {
//        User owner = User.builder()
//                .email("wallowner@example.com")
//                .build();
//
//        Wall wall = Wall.builder()
//                .owner(owner)
//                .build();
//
//        PublicMessage message = PublicMessage.builder()
//                .content("Wall message")
//                .wall(wall)
//                .build();
//
//        WallResponseDto dto = messageMapper.toWallMessageResponse(message);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getContent()).isEqualTo("Wall message");
//        assertThat(dto.getOwner()).isNotNull();
//        assertThat(dto.getOwner().getEmail()).isEqualTo("wallowner@example.com");
//    }
//
//    @Test
//    void userToUserDto_Null_ReturnsNull() {
//        assertThat(messageMapper.userToUserDto(null)).isNull();
//    }
//
//    @Test
//    void userToUserDto_Valid_ReturnsDto() {
//        User user = User.builder()
//                .email("user@example.com")
//                .build();
//
//        UserDto dto = messageMapper.userToUserDto(user);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getEmail()).isEqualTo("user@example.com");
//    }
//
//    @Test
//    void wallToUserDto_Null_ReturnsNull() {
//        assertThat(messageMapper.wallToUserDto(null)).isNull();
//    }
//
//    @Test
//    void wallToUserDto_Valid_ReturnsDto() {
//        User user = User.builder()
//                .email("walluser@example.com")
//                .build();
//
//        Wall wall = Wall.builder()
//                .owner(user)
//                .build();
//
//        UserDto dto = messageMapper.wallToUserDto(wall);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getEmail()).isEqualTo("walluser@example.com");
//    }
//
//    @Test
//    void savedMessageCommunityName_CommunityIsNull_ReturnsNull() throws Exception {
//        PublicMessage message = PublicMessage.builder()
//                .content("content")
//                .community(null)
//                .build();
//
//        Method method = MessageMapperImpl.class.getDeclaredMethod("savedMessageCommunityName", PublicMessage.class);
//        method.setAccessible(true);
//        String result = (String) method.invoke(mapper, message);
//
//        assertThat(result).isNull();
//    }
//
//    @Test
//    void savedMessageGroupChatName_GroupChatIsNull_ReturnsNull() throws Exception {
//        PublicMessage message = PublicMessage.builder()
//                .content("content")
//                .groupChat(null)
//                .build();
//
//        Method method = MessageMapperImpl.class.getDeclaredMethod("savedMessageGroupChatName", PublicMessage.class);
//        method.setAccessible(true);
//        String result = (String) method.invoke(mapper, message);
//
//        assertThat(result).isNull();
//    }
//
//    @Test
//    void wallOwnerEmail_OwnerIsNull_ReturnsNull() throws Exception {
//        Wall wall = Wall.builder().owner(null).build();
//
//        Method method = MessageMapperImpl.class.getDeclaredMethod("wallOwnerEmail", Wall.class);
//        method.setAccessible(true);
//        String result = (String) method.invoke(mapper, wall);
//
//        assertThat(result).isNull();
//    }
//}
