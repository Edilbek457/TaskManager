package org.example.consumer.service;

import lombok.RequiredArgsConstructor;
import org.example.consumer.model.ErrorInfo;
import org.example.consumer.model.Event;
import org.example.consumer.model.EventError;
import org.example.consumer.repository.EventErrorRepository;
import org.example.consumer.repository.EventRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    public final EventRepository eventRepository;
    public final EventErrorRepository eventErrorRepository;
    private final MongoTemplate mongoTemplate;


    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<EventError<ErrorInfo, Event>> getAllErrorEvent() {
        return eventErrorRepository.findAll();
    }

    public void saveRedisEventToMongo(Event event) {
        mongoTemplate.save(event, "redis-logs");
    }
}
