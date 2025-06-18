package org.example.taskFlow.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.taskFlow.enums.TaskPriority;
import org.example.taskFlow.enums.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "task")
@JsonPropertyOrder( { "id", "title", "description", "status", "priority", "deadline", "createdAt", "updatedAt", "user", "project" } )
public class Task extends BaseModel {

    @Column(name = "title", nullable = false, length = 48)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false, length = 32)
    private TaskStatus status;

    @Column(name = "priority", nullable = false, length = 32)
    private TaskPriority taskPriority;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}
