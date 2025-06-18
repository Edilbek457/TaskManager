package org.example.taskFlow.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.taskFlow.enums.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user_data")
@JsonPropertyOrder( { "id", "firstName", "lastName", "email", "active", "createdAt", "updatedAt" } )
public class User extends BaseModel {

    @Column(name = "first_name", nullable = false, length = 64)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 64)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(mappedBy = "participants")
    @JsonBackReference
    private Set<Project> projects = new HashSet<>();
}
