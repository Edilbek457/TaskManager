package org.example.consumer.app.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.consumer.model.ErrorInfo;
import org.example.consumer.model.ErrorStatusMap;
import org.example.consumer.model.Event;
import org.example.consumer.model.EventError;
import org.example.consumer.repository.EventErrorRepository;
import org.example.consumer.repository.EventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventListener {

    private final EventRepository eventLogRepository;
    private final EventErrorRepository eventErrorRepository;
    public static final String TOPIC_EVENT_TOPIC = "taskflow.events";
    public static final String TOPIC_DLQ = "taskflow.dlq";
    public static final String TOPIC_ERROR = "taskflow.error";
    public static final int TEST_STATUS_CODE = 100;

    public EventListener(EventRepository eventLogRepository, EventErrorRepository eventErrorRepository) {
        this.eventLogRepository = eventLogRepository;
        this.eventErrorRepository = eventErrorRepository;
    }

    @KafkaListener(topics = TOPIC_EVENT_TOPIC, groupId = "log-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenEvent(Event event) {
        try {
            Event savedEvent = eventLogRepository.save(event);
            log.info("Event успешно сохранён: {}", savedEvent);
        } catch (Exception e) {
            log.error("Ошибка при сохранении Event: {}", event, e);
        }
    }

    @KafkaListener(topics = TOPIC_DLQ, groupId = "dlq-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenFromDLQ(Event event) {
        log.warn("Получено сообщение из DLQ: {}", event);
        ErrorInfo errorInfo = new ErrorInfo(TEST_STATUS_CODE, ErrorStatusMap.getMessage(TEST_STATUS_CODE));
        EventError<ErrorInfo, Event> eventError = new EventError<>(errorInfo, event);
        eventErrorRepository.save(eventError);
    }
}

