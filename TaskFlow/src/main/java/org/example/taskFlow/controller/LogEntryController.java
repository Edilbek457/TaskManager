package org.example.taskFlow.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.model.LogEntry;
import org.example.taskFlow.service.LogEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LogEntryController {

    private final LogEntryService logEntryService;

    @GetMapping(value = "/logs", params = "!level")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<LogEntry>> getLogs () {
        List<LogEntry> logEntries = logEntryService.getLogEntries();
        return ResponseEntity.status(HttpStatus.OK).body(logEntries);
    }

    @GetMapping(value = "/logs", params = "level")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<LogEntry>> getLog (@RequestParam String logsLevel) {
        List<LogEntry> logEntries = logEntryService.getLogEntriesByLevel(logsLevel);
        return ResponseEntity.status(HttpStatus.OK).body(logEntries);
    }
}
