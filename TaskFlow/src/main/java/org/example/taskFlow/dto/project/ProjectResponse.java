package org.example.taskFlow.dto.project;

import org.example.taskFlow.dto.user_security.UserResponse;
import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.model.Project;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProjectResponse (

        long id,
        String name,
        String description,
        ProjectStatus status,
        UUID ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<UserResponse> participants

) {

    public static ProjectResponse from (Project project) {
        Set<UserResponse> userResponses = project.getParticipants().stream()
                .map(UserResponse::from)
                .collect(Collectors.toSet());

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getOwnerId(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                userResponses
        );
    }
}
