package org.example.taskFlow.integration.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskFlow.app.rabbitmq.RabbitConfig;
import org.example.taskFlow.dto.taskHistory.TaskHistoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
@Import(RabbitConfig.class)
public class TestRabbitMQ {

    static final String TEST_QUEUE = RabbitConfig.TEST_QUEUE;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @BeforeEach
    public void setup() {
        rabbitAdmin.initialize();
    }

    @BeforeEach
    void clearQueues() {
        rabbitTemplate.execute(channel -> {
            channel.queueDelete("task-history-test-queue");
            return null;
        });
    }

    @Test
    void sendMessageToQueue_whenValidData_thenSuccess() throws Exception {
        TaskHistoryRequest request = new TaskHistoryRequest(123L, "CREATE", 1L, Map.of());

        amqpTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, request);
        Message message = rabbitTemplate.receive(TEST_QUEUE, 10_000);
        assertThat(message).isNotNull();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();

        TaskHistoryRequest received = mapper.readValue(body, TaskHistoryRequest.class);
        assertThat(received.taskId()).isEqualTo(123L);
        assertThat(received.action()).isEqualTo("CREATE");
        assertThat(received.performedBy()).isEqualTo(1L);
        assertThat(received.details()).isNotNull();
    }
}