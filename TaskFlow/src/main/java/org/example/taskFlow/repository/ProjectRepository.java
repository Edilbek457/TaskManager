package org.example.taskFlow.repository;

import org.example.taskFlow.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN p.participants u WHERE u.id = :userId")
    List<Project> findAllByParticipantId(@Param("userId") long userId);
}
