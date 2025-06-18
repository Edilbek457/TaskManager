package org.example.taskFlow.app.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaLogger {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    KafkaLogger(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void log(Object message) {
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_LOG, message);
    }

}

