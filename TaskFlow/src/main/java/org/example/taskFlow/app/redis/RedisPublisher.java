package org.example.taskFlow.app.redis;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.model.Event;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String channel, Event event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
