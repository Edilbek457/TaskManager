package org.example.taskFlow.repository;

import org.bson.types.ObjectId;
import org.example.taskFlow.model.Log;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends MongoRepository<Log<Object>, ObjectId> {
    
}
