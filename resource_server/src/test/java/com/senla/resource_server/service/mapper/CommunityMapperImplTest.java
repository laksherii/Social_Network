//package com.senla.resource_server.service.mapper;
//
//import com.senla.resource_server.data.entity.Community;
//import com.senla.resource_server.data.entity.PublicMessage;
//import com.senla.resource_server.service.dto.community.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class CommunityMapperImplTest {
//
//    private CommunityMapperImpl mapper;
//
//    @BeforeEach
//    void setUp() {
//        mapper = new CommunityMapperImpl();
//    }
//
//    @Test
//    void toCreateCommunityResponseDto_shouldReturnNull_whenInputIsNull() {
//        // given
//        Community community = null;
//
//        // when
//        CreateCommunityResponseDto dto = mapper.toCreateCommunityResponseDto(community);
//
//        // then
//        assertThat(dto).isNull();
//    }
//
//    @Test
//    void toCreateCommunityResponseDto_shouldMapCorrectly_whenCommunityIsNotNull() {
//        // given
//        Community community = Community.builder().name("TestCommunity").build();
//
//        // when
//        CreateCommunityResponseDto dto = mapper.toCreateCommunityResponseDto(community);
//
//        // then
//        assertThat(dto).isNotNull();
//        assertThat(dto.getCommunityName()).isEqualTo("TestCommunity");
//    }
//
//    @Test
//    void toJoinCommunityResponseDto_shouldReturnNull_whenInputIsNull() {
//        // given
//        Community community = null;
//
//        // when
//        JoinCommunityResponseDto dto = mapper.toJoinCommunityResponseDto(community);
//
//        // then
//        assertThat(dto).isNull();
//    }
//
//    @Test
//    void toJoinCommunityResponseDto_shouldReturnDtoWithNullMessages_whenCommunityHasNullMessages() {
//        // given
//        Community community = Community.builder()
//                .name("Java")
//                .description("Java description")
//                .messages(null)
//                .build();
//
//        // when
//        JoinCommunityResponseDto dto = mapper.toJoinCommunityResponseDto(community);
//
//        // then
//        assertThat(dto).isNotNull();
//        assertThat(dto.getCommunityName()).isEqualTo("Java");
//        assertThat(dto.getDescription()).isEqualTo("Java description");
//        assertThat(dto.getMessages()).isNull();
//    }
//
//    @Test
//    void toCommunityDto_shouldReturnNull_whenInputIsNull() {
//        // given
//        Community community = null;
//
//        // when
//        CommunityDto dto = mapper.toCommunityDto(community);
//
//        // then
//        assertThat(dto).isNull();
//    }
//
//    @Test
//    void toCommunityDto_shouldMapCorrectly_whenCommunityIsNotNull() {
//        // given
//        Community community = Community.builder()
//                .name("Kotlin")
//                .description("Kotlin group")
//                .build();
//
//        // when
//        CommunityDto dto = mapper.toCommunityDto(community);
//
//        // then
//        assertThat(dto).isNotNull();
//        assertThat(dto.getName()).isEqualTo("Kotlin");
//        assertThat(dto.getDescription()).isEqualTo("Kotlin group");
//    }
//
//    @Test
//    void publicMessageToCommunityMessageDto_shouldReturnNull_whenInputIsNull() {
//        // given
//        PublicMessage message = null;
//
//        // when
//        CommunityMessageDto dto = mapper.publicMessageToCommunityMessageDto(message);
//
//        // then
//        assertThat(dto).isNull();
//    }
//
//    @Test
//    void publicMessageToCommunityMessageDto_shouldMapCorrectly_whenMessageIsNotNull() {
//        // given
//        PublicMessage message = PublicMessage.builder()
//                .content("Simple content")
//                .build();
//
//        // when
//        CommunityMessageDto dto = mapper.publicMessageToCommunityMessageDto(message);
//
//        // then
//        assertThat(dto).isNotNull();
//        assertThat(dto.getContent()).isEqualTo("Simple content");
//    }
//
//    @Test
//    void publicMessageListToCommunityMessageDtoList_shouldReturnNull_whenInputIsNull() {
//        // given
//        List<PublicMessage> messages = null;
//
//        // when
//        List<CommunityMessageDto> dtoList = mapper.publicMessageListToCommunityMessageDtoList(messages);
//
//        // then
//        assertThat(dtoList).isNull();
//    }
//
//    @Test
//    void publicMessageListToCommunityMessageDtoList_shouldMapCorrectly_whenListIsNotNull() {
//        // given
//        PublicMessage m1 = PublicMessage.builder().content("m1").build();
//        PublicMessage m2 = PublicMessage.builder().content("m2").build();
//
//        List<PublicMessage> messages = List.of(m1, m2);
//
//        // when
//        List<CommunityMessageDto> result = mapper.publicMessageListToCommunityMessageDtoList(messages);
//
//        // then
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getContent()).isEqualTo("m1");
//        assertThat(result.get(1).getContent()).isEqualTo("m2");
//    }
//}
