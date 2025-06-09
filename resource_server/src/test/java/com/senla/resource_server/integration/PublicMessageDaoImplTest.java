package com.senla.resource_server.integration;

import com.senla.resource_server.data.dao.impl.CommunityDaoImpl;
import com.senla.resource_server.data.dao.impl.GroupChatDaoImpl;
import com.senla.resource_server.data.dao.impl.PublicMessageDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.PublicMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.util.PaginationUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        PublicMessageDaoImpl.class,
        UserDaoImpl.class,
        CommunityDaoImpl.class,
        GroupChatDaoImpl.class,
        TestDataConfig.class,
        PaginationUtil.class
})
public class PublicMessageDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private PublicMessageDaoImpl messageDao;

    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private CommunityDaoImpl communityDao;

    @Autowired
    private GroupChatDaoImpl groupChatDao;

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/publicMessageDao/insert_wall_message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void save_withWall() {
        User sender = userDao.findById(1L).orElseThrow();
        Wall wall = sender.getWall();

        PublicMessage message = PublicMessage.builder()
                .sender(sender)
                .wall(wall)
                .content("Message to wall")
                .createdAt(Instant.now())
                .build();

        PublicMessage saved = messageDao.save(message);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getWall().getId()).isEqualTo(1L);
        assertThat(saved.getContent()).isEqualTo("Message to wall");
    }

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:publicMessageDao/insert_community_message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void save_withCommunity() {
        User sender = userDao.findById(1L).orElseThrow();
        Community community = communityDao.findById(1L).orElseThrow();

        PublicMessage message = PublicMessage.builder()
                .sender(sender)
                .community(community)
                .content("Message to community")
                .createdAt(Instant.now())
                .build();

        PublicMessage saved = messageDao.save(message);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCommunity().getId()).isEqualTo(1L);
        assertThat(saved.getContent()).isEqualTo("Message to community");
    }

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:publicMessageDao/insert_group_chat_message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void save_withGroupChat() {
        User sender = userDao.findById(1L).orElseThrow();
        GroupChat chat = groupChatDao.findById(1L).orElseThrow();

        PublicMessage message = PublicMessage.builder()
                .sender(sender)
                .groupChat(chat)
                .content("Message to group chat")
                .createdAt(Instant.now())
                .build();

        PublicMessage saved = messageDao.save(message);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getGroupChat().getId()).isEqualTo(1L);
        assertThat(saved.getContent()).isEqualTo("Message to group chat");
    }
}
