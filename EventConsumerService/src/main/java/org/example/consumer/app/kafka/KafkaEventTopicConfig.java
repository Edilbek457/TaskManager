package org.example.consumer.app.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaEventTopicConfig {

    public static final String TOPIC_EVENT = "taskflow.events";

    @Bean
    public NewTopic taskFlowLogsTopic() {
        return new NewTopic(TOPIC_EVENT, 1, (short) 1);
    }
}
