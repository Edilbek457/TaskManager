package org.example.taskFlow.integration.elastic_search;

import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ElasticSearchContainerTest extends AbstractElasticsearchTest {

    Logger log = Logger.getLogger(ElasticSearchContainerTest.class.getName());

    @Test
    void testPostgresSqlContainerIsRunning() {
        assertThat(getPostgreSQLContainer().isRunning()).isTrue();
        log.info("Тестовый контейнер PostgresSQL запущен на порту: " + getPostgreSQLContainer().getFirstMappedPort());
    }

    @Test
    void testElasticSearchContainerIsRunning() {
        assertThat(elasticsearchContainer.isRunning()).isTrue();
        log.info("Тестовый контейнер ElasticSearch запушен на порту: " + elasticsearchContainer.getHttpHostAddress());
    }
}
