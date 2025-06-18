package org.example.taskFlow.controller;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.taskFlow.model.Log;
import org.example.taskFlow.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("events")
public class LogController {

    private final LogService logService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Log<Object>>> getAllEvent() {
        return ResponseEntity.status(HttpStatus.OK).body(logService.getListLogs());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Log<Object>> getEventById(@PathVariable("id") ObjectId id) {
        return ResponseEntity.status(HttpStatus.OK).body(logService.getEventById(id));
    }
}
