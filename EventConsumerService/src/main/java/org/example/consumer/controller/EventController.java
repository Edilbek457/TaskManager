package org.example.consumer.controller;

import lombok.RequiredArgsConstructor;
import org.example.consumer.model.ErrorInfo;
import org.example.consumer.model.Event;
import org.example.consumer.model.EventError;
import org.example.consumer.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/processed-events")
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEvents());
    }

    @GetMapping("/errors")
    public ResponseEntity<List<EventError<ErrorInfo, Event>>> getErrorEvents() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllErrorEvent());
    }
}
