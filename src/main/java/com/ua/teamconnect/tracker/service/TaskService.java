package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.TaskMapper;
import com.ua.teamconnect.tracker.model.dto.AssignTaskRequestDto;
import com.ua.teamconnect.tracker.model.dto.TaskDto;
import com.ua.teamconnect.tracker.model.dto.TaskRequestDto;
import com.ua.teamconnect.tracker.model.entity.Project;
import com.ua.teamconnect.tracker.model.entity.Task;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.UserProjectTask;
import com.ua.teamconnect.tracker.model.entity.id.UserProjectId;
import com.ua.teamconnect.tracker.model.entity.id.UserProjectTaskId;
import com.ua.teamconnect.tracker.model.exception.DuplicateException;
import com.ua.teamconnect.tracker.model.exception.NotFoundException;
import com.ua.teamconnect.tracker.model.exception.UserProjectAssignmentException;
import com.ua.teamconnect.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserProjectTaskRepository userProjectTaskRepository;
    private final TaskMapper taskMapper;

    public TaskDto create(TaskRequestDto requestDto) {
        if (taskRepository.existsByName(requestDto.name())) {
            throw DuplicateException.task();
        }
        var entity = new Task();
        taskMapper.requestDtoToEntity(requestDto, entity);
        entity = taskRepository.save(entity);
        return taskMapper.entityToDto(entity);
    }

    public TaskDto update(Integer id, TaskRequestDto requestDto) {
        if (taskRepository.existsByNameAndIdNot(requestDto.name(), id)) {
            throw DuplicateException.task();
        }
        var entity = taskRepository.findById(id).orElseThrow(
            () -> NotFoundException.task(id)
        );
        taskMapper.requestDtoToEntity(requestDto, entity);
        entity = taskRepository.save(entity);
        return taskMapper.entityToDto(entity);
    }

    @Transactional
    public void assignTaskToUsersProject(Integer userId, Integer projectId, AssignTaskRequestDto requestDto) {
        var user = userRepository.findById(userId).orElseThrow(
            () -> NotFoundException.userById(userId)
        );
        var project = projectRepository.findById(projectId).orElseThrow(
            () -> NotFoundException.project(projectId)
        );
        var userProjectId = UserProjectId.of(user, project);
        if (!userProjectRepository.existsByIdAndNow(userProjectId, LocalDate.now())) {
            throw new UserProjectAssignmentException();
        }
        requestDto.taskIds().stream()
            .map(this::findTaskById)
            .flatMap(task -> findNotAssignedTasks(task, user, project))
            .forEach(userProjectTaskRepository::save);
    }

    private Task findTaskById(Integer id) {
        return taskRepository.findById(id).orElseThrow(
            () -> NotFoundException.task(id)
        );
    }

    private Stream<UserProjectTask> findNotAssignedTasks(Task task, User user, Project project) {
        var id = UserProjectTaskId.of(user, project, task);
        if (userProjectTaskRepository.existsById(id)) return Stream.empty();
        return Stream.of(UserProjectTask.of(user, project, task));
    }
}
