package org.example.taskFlow.model;

import jakarta.persistence.Id;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "log_entry")
public class LogEntry {

    @Id
    private ObjectId id;

    private String level;

    private String message;

    @CreatedDate
    private LocalDateTime timestamp;

    private Map<String, Object> context;

}
