package org.example.taskFlow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.project.ProjectRequest;
import org.example.taskFlow.dto.project.ProjectResponse;
import org.example.taskFlow.model.Project;
import org.example.taskFlow.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> getProject (@PathVariable long id) {
        Project project = projectService.getProjectById(id);
        ProjectResponse projectResponse = ProjectResponse.from(project);
        return ResponseEntity.status(HttpStatus.OK).body(projectResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sortBy,
                                                                @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Project> projectPage = projectService.getAllProjects(pageable);
        Page<ProjectResponse> projectResponses = projectPage.map(ProjectResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(projectResponses);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest) {
        Project project = projectService.saveProject(projectRequest);
        ProjectResponse projectResponse = ProjectResponse.from(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable long id, @RequestBody @Valid ProjectRequest projectRequest) {
        Project project = projectService.updateProject(id, projectRequest);
        ProjectResponse projectResponse = ProjectResponse.from(project);
        return ResponseEntity.status(HttpStatus.OK).body(projectResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> deleteProject(@PathVariable long id) {
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping("/{projectId}/users/{userId}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> addUserToProject(@PathVariable long projectId, @PathVariable long userId) {
        Project project = projectService.addUserToProject(projectId, userId);
        ProjectResponse projectResponse = ProjectResponse.from(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponse);
    }
}
