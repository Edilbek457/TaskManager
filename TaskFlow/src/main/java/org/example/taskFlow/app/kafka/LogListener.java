package org.example.taskFlow.app.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.taskFlow.model.Log;
import org.example.taskFlow.repository.LogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogListener {

    private final LogRepository logEventRepository;

    public LogListener(LogRepository logEventRepository) {
        this.logEventRepository = logEventRepository;
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_LOG, groupId = "log-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenLogEvent(Log<Object> logs) {
        try {
            Log<Object> savedLog = logEventRepository.save(logs);
            log.info("Log успешно сохранён: {}", savedLog);
        } catch (Exception e) {
            log.error("Ошибка при сохранении Log: {}", logs, e);
        }
    }
}


