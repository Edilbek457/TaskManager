package org.example.taskFlow.integration.postgres_sql;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostgresSQLContainerTest extends AbstractPostgresSQLTest {

    private static final Logger log = LoggerFactory.getLogger(PostgresSQLContainerTest.class);

    @Test
    void testPostgresSqlContainerIsRunning() {
        assertThat(getPostgreSQLContainer().isRunning()).isTrue();
        log.info("Тестовый контейнер PostgresSQL запущен на порту: " + getPostgreSQLContainer().getFirstMappedPort());
    }
}