package org.example.taskFlow.exception.event;

import org.bson.types.ObjectId;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException (ObjectId eventId) {
        super(String.format(
                "Событие с Id: %s не найдено", eventId
        ));
    }
}
