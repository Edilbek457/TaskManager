package org.example.taskFlow.model;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Document(collection = "task_history")
public class TaskHistory {

    @Id
    private ObjectId id;

    private long taskId;

    private String action;

    private long performedBy;

    @CreatedDate
    private LocalDateTime timestamp;

    private Map<String, Object> details;

}
