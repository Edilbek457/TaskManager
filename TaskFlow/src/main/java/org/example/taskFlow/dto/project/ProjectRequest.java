package org.example.taskFlow.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.model.Project;
import java.util.UUID;

public record ProjectRequest (

       @NotBlank(message = "Название проекта не должно быть пустым")
       @Size(max = 64, message = "Название проекта не должно превышать 64 символа")
       String name,

       @NotBlank(message = "Описание проекта не может быть пустым")
       String description,

       @NotNull(message = "Статус проекта не указан")
       ProjectStatus status,

       UUID ownerId

) {
       public static ProjectRequest from(Project project) {
              return new ProjectRequest(
                      project.getName(),
                      project.getDescription(),
                      project.getStatus(),
                      project.getOwnerId()
              );
       }

}
