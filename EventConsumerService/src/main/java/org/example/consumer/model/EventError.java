package org.example.consumer.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collation = "event_error_log")
public class EventError<ErrorInfo, Event> {

    @Id
    private ObjectId id;
    private ErrorInfo error;
    private Event event;

    public EventError(ErrorInfo errorInfo, Event event) {
        this.error = errorInfo;
        this.event = event;
    }
}
