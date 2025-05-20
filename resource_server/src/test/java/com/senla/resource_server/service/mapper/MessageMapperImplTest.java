package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class MessageMapperImplTest {

    private final MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Test
    void toPrivateMessageResponse_NullMessage_ReturnsNull() {
        assertThat(messageMapper.toPrivateMessageResponse(null)).isNull();
    }

    @Test
    void toPrivateMessageResponse_ValidMessage_ReturnsDto() {
        User sender = User.builder()
                .email("sender@example.com")
                .build();

        PrivateMessage message = PrivateMessage.builder()
                .content("hello")
                .sender(sender)
                .build();

        PrivateMessageResponseDto dto = messageMapper.toPrivateMessageResponse(message);

        assertThat(dto).isNotNull();
        assertThat(dto.getContent()).isEqualTo("hello");
        assertThat(dto.getSender()).isNotNull();
        assertThat(dto.getSender().getEmail()).isEqualTo("sender@example.com");
    }

    @Test
    void toCommunityMessageResponse_NullMessage_ReturnsNull() {
        assertThat(messageMapper.toCommunityMessageResponse(null)).isNull();
    }

    @Test
    void toCommunityMessageResponse_ValidMessage_ReturnsDto() {
        Community community = Community.builder()
                .name("TestCommunity")
                .build();

        PublicMessage message = PublicMessage.builder()
                .content("Message to community")
                .community(community)
                .build();

        SendCommunityMessageResponseDto dto = messageMapper.toCommunityMessageResponse(message);

        assertThat(dto).isNotNull();
        assertThat(dto.getContent()).isEqualTo("Message to community");
        assertThat(dto.getCommunityName()).isEqualTo("TestCommunity");
    }

    @Test
    void toGroupChatMessageResponse_NullMessage_ReturnsNull() {
        assertThat(messageMapper.toGroupChatMessageResponse(null)).isNull();
    }

    @Test
    void toGroupChatMessageResponse_ValidMessage_ReturnsDto() {
        GroupChat chat = GroupChat.builder()
                .name("TestGroup")
                .build();

        PublicMessage message = PublicMessage.builder()
                .content("Group message")
                .groupChat(chat)
                .build();

        GroupChatMessageResponseDto dto = messageMapper.toGroupChatMessageResponse(message);

        assertThat(dto).isNotNull();
        assertThat(dto.getContent()).isEqualTo("Group message");
        assertThat(dto.getName()).isEqualTo("TestGroup");
    }

    @Test
    void toWallMessageResponse_NullMessage_ReturnsNull() {
        assertThat(messageMapper.toWallMessageResponse(null)).isNull();
    }

    @Test
    void toWallMessageResponse_ValidMessage_ReturnsDto() {
        User owner = User.builder()
                .email("wallowner@example.com")
                .build();

        Wall wall = Wall.builder()
                .owner(owner)
                .build();

        PublicMessage message = PublicMessage.builder()
                .content("Wall message")
                .wall(wall)
                .build();

        WallResponseDto dto = messageMapper.toWallMessageResponse(message);

        assertThat(dto).isNotNull();
        assertThat(dto.getContent()).isEqualTo("Wall message");
        assertThat(dto.getOwner()).isNotNull();
        assertThat(dto.getOwner().getEmail()).isEqualTo("wallowner@example.com");
    }
}
