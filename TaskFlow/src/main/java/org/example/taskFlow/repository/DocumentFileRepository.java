package org.example.taskFlow.repository;

import org.bson.types.ObjectId;
import org.example.taskFlow.model.DocumentFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentFileRepository extends MongoRepository<DocumentFile, ObjectId> {
    List<DocumentFile> findDocumentFilesByTaskId (long id);
}
