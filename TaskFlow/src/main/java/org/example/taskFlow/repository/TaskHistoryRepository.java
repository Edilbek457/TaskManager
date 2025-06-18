package org.example.taskFlow.repository;

import org.bson.types.ObjectId;
import org.example.taskFlow.model.TaskHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends MongoRepository<TaskHistory, ObjectId> {
    List<TaskHistory> findTasksHistoriesById (ObjectId taskId);
}
