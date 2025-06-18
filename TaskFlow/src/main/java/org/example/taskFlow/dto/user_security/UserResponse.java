package org.example.taskFlow.dto.user_security;
import org.example.taskFlow.enums.Role;
import org.example.taskFlow.model.User;
import java.time.LocalDateTime;

public record UserResponse (
        long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from (User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(), user.isActive(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
