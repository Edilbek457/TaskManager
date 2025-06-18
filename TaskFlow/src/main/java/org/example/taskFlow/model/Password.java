package org.example.taskFlow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.taskFlow.enums.PasswordStrengthLevel;

@Getter
@Setter
@Entity
@Table(name = "password")
public class Password extends BaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private byte passwordStrengthLevel;

    @Column(nullable = false)
    private PasswordStrengthLevel passwordStrengthLevelType;
}
