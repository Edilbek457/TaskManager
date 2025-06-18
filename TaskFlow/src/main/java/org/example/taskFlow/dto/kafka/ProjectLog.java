package org.example.taskFlow.dto.kafka;

import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.model.Project;
import java.io.Serializable;

public record ProjectLog(long id, String name, ProjectStatus status) implements Serializable {

    public static ProjectLog toProjectLog(Project project) {
        return new ProjectLog(project.getId(), project.getName(), project.getStatus());
    }

}
