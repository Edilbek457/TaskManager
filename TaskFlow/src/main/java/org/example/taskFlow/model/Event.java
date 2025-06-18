package org.example.taskFlow.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Event {

    private String eventType;
    private UUID entityId;
    private String entityType;
    private String payload;
    private LocalDateTime createdAt;

    public Event(String eventType, UUID entityId, String entityType, String payload) {
        this.eventType = eventType;
        this.entityId = entityId;
        this.entityType = entityType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }
}
