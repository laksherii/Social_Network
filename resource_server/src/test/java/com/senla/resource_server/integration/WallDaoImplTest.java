package com.senla.resource_server.integration;

import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.dao.impl.WallDaoImpl;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.Wall;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {WallDaoImpl.class, UserDaoImpl.class, TestDataConfig.class})
public class WallDaoImplTest extends BaseIntegrationTest {

    @Autowired
    WallDaoImpl wallDao;

    @Autowired
    UserDaoImpl userDao;

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @Transactional
    public void save() {

        User user = userDao.findById(1L).orElseThrow();

        Wall wall = Wall.builder()
                .owner(user)
                .build();

        Wall save = wallDao.save(wall);
        assertThat(save.getOwner()).isEqualTo(user);
    }
}
