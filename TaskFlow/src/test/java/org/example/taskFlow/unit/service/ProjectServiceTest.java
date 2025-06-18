package org.example.taskFlow.unit.service;

import org.example.taskFlow.dto.project.ProjectRequest;
import org.example.taskFlow.enums.ProjectStatus;
import org.example.taskFlow.exception.project.ProjectNotFoundException;
import org.example.taskFlow.exception.user.UserNotFoundException;
import org.example.taskFlow.model.oldModel.Project;
import org.example.taskFlow.model.oldModel.User;
import org.example.taskFlow.repository.oldRepository.ProjectRepository;
import org.example.taskFlow.service.oldService.ProjectService;
import org.example.taskFlow.service.oldService.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private final static long PROJECT_ID = 1L;
    private final static UUID PROJECT_OWNER_ID = UUID.randomUUID();

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    public void getAllProject_whenProjectCount_20_thenGet_20_projects() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Project project = new Project();
            projects.add(project);
        }
        Page<Project> mockPage = new PageImpl<>(projects, pageable, projects.size());
        when(projectRepository.findAll(pageable)).thenReturn(mockPage);
        Page<Project> result = projectService.getAllProjects(pageable);

        assertNotNull(result);
        assertEquals(20, result.getContent().size());
        verify(projectRepository).findAll(pageable);
    }

    @Test
    public void getProjectById_whenProjectDoesNotExist_thenThrowException() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(PROJECT_ID));
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    public void getProjectById_whenProjectExists_thenGetProject() {
        Project project = new Project();
        project.setId(PROJECT_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        Project projectResult = projectService.getProjectById(PROJECT_ID);

        assertNotNull(projectResult);
        assertEquals(PROJECT_ID, projectResult.getId());
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    public void saveProject_thenSaveProject() {
        ProjectRequest projectRequest = new ProjectRequest("Name", "Description", ProjectStatus.ARCHIVED, PROJECT_OWNER_ID);
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setName("Name");
        project.setDescription("Description");
        project.setStatus(ProjectStatus.ARCHIVED);
        project.setOwnerId(PROJECT_OWNER_ID);

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        Project savedProject = projectService.saveProject(projectRequest);

        assertNotNull(savedProject);
        assertEquals(project.getId(), savedProject.getId());
        assertEquals(project.getName(), savedProject.getName());
        assertEquals(project.getDescription(), savedProject.getDescription());
        assertEquals(project.getStatus(), savedProject.getStatus());
        assertEquals(project.getOwnerId(), savedProject.getOwnerId());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    public void createProject_thenCreateProject() {
        ProjectRequest projectRequest = new ProjectRequest("Name", "Description", ProjectStatus.ARCHIVED, PROJECT_OWNER_ID);

        Project savedProject = projectService.createProject(projectRequest);

        assertNotNull(savedProject);
        assertEquals(projectRequest.name(), savedProject.getName());
        assertEquals(projectRequest.description(), savedProject.getDescription());
        assertEquals(projectRequest.status(), savedProject.getStatus());
        assertEquals(projectRequest.ownerId(), savedProject.getOwnerId());
    }

    @Test
    public void updateProject_whenProjectExist_thenUpdateProject() {
        ProjectRequest projectRequest = new ProjectRequest("UpdatedName", "UpdatedDescription", ProjectStatus.ARCHIVED, PROJECT_OWNER_ID);
        Project existingProject = new Project();
        existingProject.setId(PROJECT_ID);
        existingProject.setName("Name");
        existingProject.setDescription("Description");
        existingProject.setStatus(ProjectStatus.ACTIVE);
        existingProject.setOwnerId(UUID.randomUUID());
        Project updatedProject = new Project();
        updatedProject.setId(PROJECT_ID);
        updatedProject.setName(projectRequest.name());
        updatedProject.setDescription(projectRequest.description());
        updatedProject.setStatus(projectRequest.status());
        updatedProject.setOwnerId(projectRequest.ownerId());

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        Project projectResult = projectService.updateProject(PROJECT_ID, projectRequest);

        assertNotNull(projectResult);
        assertEquals(projectRequest.name(), projectResult.getName());
        assertEquals(projectRequest.description(), projectResult.getDescription());
        assertEquals(projectRequest.status(), projectResult.getStatus());
        assertEquals(projectRequest.ownerId(), projectResult.getOwnerId());
        verify(projectRepository).findById(PROJECT_ID);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    public void updateProject_whenProjectDoesNotExist_thenThrowException() {
        ProjectRequest projectRequest = new ProjectRequest("UpdatedName", "UpdatedDescription", ProjectStatus.ACTIVE, PROJECT_OWNER_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(PROJECT_ID, projectRequest));
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    public void deleteProject_whenProjectExist_thenDeleteProject() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(new Project()));
        projectService.deleteProject(PROJECT_ID);

        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    public void deleteProject_whenProjectNotExist_thenThrowException() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(PROJECT_ID));
        verify(projectRepository).findById(PROJECT_ID);
        verify(projectRepository, never()).deleteById(anyLong());
    }

    @Test
    public void addUserToProject_whenProjectAndUserExist_thenAddUserToProject() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("email@gamil.com");
        Project updatedProject = new Project();
        updatedProject.setId(PROJECT_ID);
        updatedProject.setName("UpdatedName");
        updatedProject.setDescription("UpdatedDescription");
        updatedProject.setStatus(ProjectStatus.ACTIVE);
        updatedProject.getParticipants().add(user);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(new Project()));
        when(userService.getUserById(1L)).thenReturn(user);
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        Project projectResult = projectService.addUserToProject(PROJECT_ID, 1L);



        assertNotNull(projectResult.getParticipants());
        assertEquals(projectResult.getParticipants().size(), 1);
        assertTrue(projectResult.getParticipants().contains(user));
        assertEquals(projectResult.getParticipants().stream().toList().getFirst().getId(), user.getId());
        assertEquals(projectResult.getParticipants().stream().toList().getFirst().getFirstName(), user.getFirstName());
        assertEquals(projectResult.getParticipants().stream().toList().getFirst().getLastName(), user.getLastName());
        assertEquals(projectResult.getParticipants().stream().toList().getFirst().getEmail(), user.getEmail());
        verify(projectRepository).findById(PROJECT_ID);
        verify(userService).getUserById(1L);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    public void addUserToProject_whenProjectDoesNotExist_thenThrowException() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.addUserToProject(PROJECT_ID, 1L));
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    public void addUserToProject_whenUserDoesExist_thenThrowException() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(new Project()));
        when(userService.getUserById(1L)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> projectService.addUserToProject(PROJECT_ID, 1L));
        verify(projectRepository).findById(PROJECT_ID);
        verify(userService).getUserById(1L);
    }
}

