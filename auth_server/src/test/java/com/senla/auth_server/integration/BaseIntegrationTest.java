package com.senla.auth_server.integration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
public class BaseIntegrationTest {

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    private static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }
}
