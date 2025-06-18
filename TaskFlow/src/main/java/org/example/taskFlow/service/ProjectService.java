package org.example.taskFlow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.taskFlow.app.redis.RedisPublisher;
import org.example.taskFlow.dto.project.ProjectRequest;
import org.example.taskFlow.enums.LogLevel;
import org.example.taskFlow.enums.RedisChannel;
import org.example.taskFlow.exception.project.ProjectNotFoundException;
import org.example.taskFlow.model.Event;
import org.example.taskFlow.model.Project;
import org.example.taskFlow.model.User;
import org.example.taskFlow.repository.ProjectRepository;
import org.example.taskFlow.service.elastic_search.ProjectIndexService;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final LogService logEventService;
    private final EventService eventService;
    private final ProjectIndexService projectIndexService;
    private final RedisPublisher redisPublisher;

    public Page<Project> getAllProjects (Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    public Project getProjectById (long id) {
        Object cachedProject = redisTemplate.opsForValue().get(String.valueOf(id));
        if (cachedProject != null) {
            return objectMapper.convertValue(cachedProject,  Project.class);
        } else {
            Optional<Project> foundedProject = projectRepository.findById(id);
            if (foundedProject.isPresent()) {
                redisTemplate.opsForValue().set(String.valueOf(id), foundedProject, 60, TimeUnit.MINUTES);
                return foundedProject.get();
            } else {
                throw new ProjectNotFoundException(id);
            }
        }
    }

    public Project saveProject(ProjectRequest projectRequest) {
        Project project = createProject(projectRequest);
        Project savedProject = projectRepository.save(project);
        projectIndexService.saveProjectIndex(ProjectIndexService.toProjectIndex(project));
        redisTemplate.opsForValue().set(String.valueOf(project.getId()), savedProject, 60, TimeUnit.MINUTES);
        logEventService.createAndSendLog(LogLevel.INFO, "Project saved", savedProject);
        Event createdEvent = eventService.createEvent("PROJECT_CREATED", UUID_Service.fromLong(project.getId()), "PROJECT", null, project);
        eventService.sentKafkaEvent(createdEvent);
        redisPublisher.publish(RedisChannel.PROJECT_CREATE.getChannel(), createdEvent);
        return savedProject;
    }

    public Project createProject(ProjectRequest projectRequest) {
        Project project = new Project();
        project.setName(projectRequest.name());
        project.setDescription(projectRequest.description());
        project.setStatus(projectRequest.status());
        project.setOwnerId(projectRequest.ownerId());
        return project;
    }

    @Transactional
    public Project updateProject(long id, ProjectRequest projectRequest) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        Hibernate.initialize(project.getParticipants());
        Project oldProject = new Project();
        oldProject.setName(project.getName());
        oldProject.setDescription(project.getDescription());
        oldProject.setStatus(project.getStatus());
        oldProject.setOwnerId(project.getOwnerId());

        project.setName(projectRequest.name());
        project.setDescription(projectRequest.description());
        project.setStatus(projectRequest.status());
        project.setOwnerId(projectRequest.ownerId());

        Project updatedProject = projectRepository.save(project);

        projectIndexService.updateProjectIndex(UUID_Service.fromLong(project.getId()), ProjectIndexService.toProjectIndex(project));
        redisTemplate.opsForValue().set(String.valueOf(project.getId()), updatedProject, 60, TimeUnit.MINUTES);
        logEventService.createAndSendLog(LogLevel.INFO, "Project updated", updatedProject);
        Event createdEvent = eventService.createEvent(
                "PROJECT_UPDATED",
                UUID_Service.fromLong(project.getId()),
                "PROJECT",
                ProjectRequest.from(oldProject),
                ProjectRequest.from(updatedProject)
        );
        eventService.sentKafkaEvent(createdEvent);
        redisPublisher.publish(RedisChannel.PROJECT_UPDATE.getChannel(), createdEvent);
        return updatedProject;
    }


    public void deleteProject(long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        projectIndexService.deleteProjectIndex(UUID_Service.fromLong(project.getId()));
        redisTemplate.delete(String.valueOf(project.getId()));
        projectRepository.deleteById(id);
        logEventService.createAndSendLog(LogLevel.INFO, "Project deleted", project);
        Event createdEvent = eventService.createEvent("PROJECT_DELETED", UUID_Service.fromLong(project.getId()), "PROJECT", project, null);
        eventService.sentKafkaEvent(createdEvent);
        redisPublisher.publish(RedisChannel.PROJECT_DELETE.getChannel(), createdEvent);
    }

    @Transactional
    public Project addUserToProject (long projectId, long userId) {
        Project project = getProjectById(projectId);
        User user = userService.getUserById(userId);
        project.getParticipants().add(user);
        Project savedProject = projectRepository.save(project);
        projectIndexService.updateProjectIndex(UUID_Service.fromLong(savedProject.getId()), ProjectIndexService.toProjectIndex(savedProject));
        return savedProject;
    }
}

