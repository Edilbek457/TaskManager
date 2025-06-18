package org.example.taskFlow.model.elastic_search;

import lombok.Getter;
import lombok.Setter;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document(indexName = "task_index")
public class TaskIndex {

    @Id
    private UUID id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private TaskStatus status;

    @Field(type = FieldType.Keyword)
    private TaskPriority priority;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    @Field(type = FieldType.Keyword)
    private UUID assignedUserId;

    @Field(type = FieldType.Keyword)
    private UUID projectId;
}

