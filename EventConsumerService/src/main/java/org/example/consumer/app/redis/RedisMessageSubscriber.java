package org.example.consumer.app.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.consumer.exception.event.SerializationProblem;
import org.example.consumer.model.Event;
import org.example.consumer.service.EventService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final EventService eventService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            Event event = objectMapper.readValue(body, Event.class);
            eventService.saveRedisEventToMongo(event);
            log.info("Сохранили Event из канала [{}]: {}", channel, event);
        } catch (Exception e) {
            log.error("Ошибка при сериализации, канал redis: [{}]", channel);
            throw new SerializationProblem(LocalDateTime.now());
        }
    }
}


