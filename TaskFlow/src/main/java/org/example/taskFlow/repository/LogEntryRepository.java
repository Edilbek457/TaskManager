package org.example.taskFlow.repository;

import org.bson.types.ObjectId;
import org.example.taskFlow.model.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEntryRepository extends MongoRepository<LogEntry, ObjectId> {
    List<LogEntry> findLogEntriesByLevel (String level);
}
