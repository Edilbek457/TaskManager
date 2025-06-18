package org.example.consumer.repository;

import org.bson.types.ObjectId;
import org.example.consumer.model.ErrorInfo;
import org.example.consumer.model.Event;
import org.example.consumer.model.EventError;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventErrorRepository extends MongoRepository<EventError<ErrorInfo, Event>, ObjectId> {

}
