package org.example.taskFlow.app.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC_LOG = "taskflow_log";
    public static final String TOPIC_EVENT = "taskflow.events";
    public static final String TOPIC_DLQ = "taskflow.dlq";

    @Bean
    public NewTopic taskFlowLogTopic() {
        return new NewTopic(TOPIC_LOG, 1, (short) 1);
    }

    @Bean
    public NewTopic taskFlowEventTopic() {
        return new NewTopic(TOPIC_EVENT, 1, (short) 1);
    }

    @Bean
    public NewTopic taskFlowDLQTopic() { return new NewTopic(TOPIC_DLQ, 1, (short) 1);}
}

