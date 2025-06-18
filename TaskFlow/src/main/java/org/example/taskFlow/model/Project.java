package org.example.taskFlow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.taskFlow.enums.ProjectStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "project")
public class Project extends BaseModel {

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name =  "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false, length = 32)
    private ProjectStatus status;

    @Column(name = "owner_id")
    private UUID ownerId;

    @ManyToMany
    @JoinTable(
            name = "user_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    @JsonIgnore
    @JsonManagedReference
    private Set<User> participants = new HashSet<>();
}
