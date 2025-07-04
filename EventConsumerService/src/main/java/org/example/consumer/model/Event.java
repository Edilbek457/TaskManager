package org.example.consumer.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document(collation = "event_log")
public class Event {

    @Id
    private ObjectId id;
    private String eventType;
    private UUID entityId;
    private String entityType;
    private String payload;
    private LocalDateTime createdAt;

}