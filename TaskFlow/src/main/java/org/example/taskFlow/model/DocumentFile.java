package org.example.taskFlow.model;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "document_file")
public class DocumentFile {

    @Id
    private ObjectId id;

    private long taskId;

    private String fileName;

    private String fileType;

    @CreatedDate
    private LocalDateTime uploadedAt;

    private long size;

}
