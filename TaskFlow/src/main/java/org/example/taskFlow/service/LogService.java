package org.example.taskFlow.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.taskFlow.app.kafka.KafkaLogger;
import org.example.taskFlow.enums.LogLevel;
import org.example.taskFlow.exception.event.EventNotFoundException;
import org.example.taskFlow.model.Log;
import org.example.taskFlow.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final KafkaLogger kafkaLogger;
    private final LogRepository logRepository;

    public Object createAndSendLog (LogLevel level, String message, Object context) {
        Log<?> logs = new Log<>(level, message, context);
        kafkaLogger.log(logs);
        return logs;
    }

    public List<Log<Object>> getListLogs() {
        return logRepository.findAll();
    }

    public Log<Object> getEventById(ObjectId eventId) {
        return logRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }
}
