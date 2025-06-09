package com.senla.resource_server.integration;

import com.senla.resource_server.data.dao.impl.CommunityDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.util.PaginationUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestDataConfig.class, CommunityDaoImpl.class, UserDaoImpl.class, PaginationUtil.class})
class CommunityDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private CommunityDaoImpl communityDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:communityDao/insert_community.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findById_shouldReturnCommunity() {
        Optional<Community> result = communityDao.findById(100L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(100L);
        assertThat(result.get().getName()).isEqualTo("Community 1");
    }

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:communityDao/insert_community.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void save_shouldPersistCommunity() {
        User admin = userDao.findById(1L).orElseThrow();

        Community community = Community.builder()
                .name("New Community")
                .description("New Description")
                .admin(admin)
                .build();

        Community saved = communityDao.save(community);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Community");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:communityDao/insert_community.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_shouldReturnPaginatedCommunities() {
        List<Community> result = communityDao.findAll(1, 2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Community 1");
        assertThat(result.get(1).getName()).isEqualTo("Community 2");
    }
}