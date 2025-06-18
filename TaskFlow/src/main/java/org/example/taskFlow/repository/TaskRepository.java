package org.example.taskFlow.repository;

import org.example.taskFlow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(long userId);
    Optional<Task> findTaskById (long id);
}
