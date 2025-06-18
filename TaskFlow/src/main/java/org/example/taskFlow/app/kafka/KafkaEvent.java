package org.example.taskFlow.app.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaEvent {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    KafkaEvent(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void log(Object message) {
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_EVENT, message);
    }

}