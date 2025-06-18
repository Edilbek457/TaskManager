package org.example.consumer.repository;

import org.bson.types.ObjectId;
import org.example.consumer.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {

}
