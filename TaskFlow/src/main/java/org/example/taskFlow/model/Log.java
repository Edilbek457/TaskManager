package org.example.taskFlow.model;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.example.taskFlow.enums.LogLevel;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "log_entries")
public class Log<T> implements Serializable {

    public Log(){}

    @Id
    private ObjectId id;
    private LogLevel level;
    private String message;
    private T context;
    private LocalDateTime timestamp;

    public Log(LogLevel level, String message, T context) {
        this.level = level;
        this.message = message;
        this.context = context;
        this.timestamp = LocalDateTime.now();
    }
}

