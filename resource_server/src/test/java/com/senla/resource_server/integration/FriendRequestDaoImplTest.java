package com.senla.resource_server.integration;

import com.senla.resource_server.data.dao.impl.FriendRequestDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus;
import com.senla.resource_server.data.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestDataConfig.class, UserDaoImpl.class, FriendRequestDaoImpl.class})
class FriendRequestDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private FriendRequestDaoImpl friendRequestDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:friendRequestDao/insert_friend_request.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void save_shouldPersistFriendRequest() {

        User sender = userDao.findById(2L).orElseThrow();
        User recipient = userDao.findById(3L).orElseThrow();

        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendRequestStatus.UNDEFINED)
                .build();

        FriendRequest saved = friendRequestDao.save(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSender()).isEqualTo(sender);
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:friendRequestDao/insert_friend_request.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findById_shouldReturnRequest() {
        Optional<FriendRequest> optional = friendRequestDao.findById(100L);
        assertThat(optional).isPresent();
        FriendRequest found = optional.get();
        assertThat(found.getSender().getId()).isEqualTo(1L);
        assertThat(found.getRecipient().getId()).isEqualTo(2L);
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:friendRequestDao/insert_friend_request.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<FriendRequest> optional = friendRequestDao.findById(999L);
        assertThat(optional).isEmpty();
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:friendRequestDao/insert_friend_request.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findBySenderAndRecipient_shouldReturnRequest() {
        User sender = userDao.findById(1L).orElseThrow();
        User recipient = userDao.findById(2L).orElseThrow();

        FriendRequest actual = friendRequestDao.findBySenderAndRecipient(sender, recipient).orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual.getSender().getId()).isEqualTo(sender.getId());
        assertThat(actual.getRecipient().getId()).isEqualTo(recipient.getId());
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:friendRequestDao/insert_friend_request.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findBySenderAndRecipient_shouldReturnEmpty_whenNotExists() {
        User sender = userDao.findById(1L).orElseThrow();
        User notFriend = userDao.findById(3L).orElseThrow();

        Optional<FriendRequest> optional = friendRequestDao.findBySenderAndRecipient(sender, notFriend);
        assertThat(optional).isEmpty();
    }

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:friendRequestDao/insert_friend_request.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void delete_shouldRemoveRequest() {
        friendRequestDao.delete(100L);

        Optional<FriendRequest> result = friendRequestDao.findById(100L);
        assertThat(result).isEmpty();
    }
}

