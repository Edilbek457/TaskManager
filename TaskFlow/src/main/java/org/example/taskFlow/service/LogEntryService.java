package org.example.taskFlow.service;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.model.LogEntry;
import org.example.taskFlow.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;

    public List<LogEntry> getLogEntries() {
        return logEntryRepository.findAll();
    }

    public List<LogEntry> getLogEntriesByLevel (String level) {
        return logEntryRepository.findLogEntriesByLevel(level);
    }
}
