package org.example.taskFlow.model.elastic_search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;


@Getter
@Setter
@Document(indexName = "comment_index")
public class CommentIndex {

    @Id
    private UUID id;

    @Field(type = FieldType.Text)
    private String comment;

    @Field(type = FieldType.Keyword)
    private UUID taskId;

    @Field(type = FieldType.Keyword)
    private UUID userId;

    @CreatedDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;


}

