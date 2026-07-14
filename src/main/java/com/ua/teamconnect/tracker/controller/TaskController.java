package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.AssignTaskRequestDto;
import com.ua.teamconnect.tracker.model.dto.TaskDto;
import com.ua.teamconnect.tracker.model.dto.TaskRequestDto;
import com.ua.teamconnect.tracker.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "Endpoints related to tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping(path = "/tasks")
    @PreAuthorize("hasAnyRole('PM', 'HR', 'ADMIN')")
    @Tag(name = "Create Task", description = "Creates a new task")
    public TaskDto create(@RequestBody @Valid TaskRequestDto requestDto) {
        return taskService.create(requestDto);
    }

    @PutMapping(path = "/tasks/{id}")
    @PreAuthorize("hasAnyRole('PM', 'HR', 'ADMIN')")
    @Tag(name = "Update Task", description = "Updates an existing task by ID")
    public TaskDto update(@PathVariable Integer id, @RequestBody @Valid TaskRequestDto requestDto) {
        return taskService.update(id, requestDto);
    }

    @PostMapping("/users/{userId}/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('PM', 'HR', 'ADMIN')")
    @Tag(name = "Assign Task", description = "Assigns a task to a specific project and user")
    public void assignTaskToUsersProject(
        @PathVariable Integer userId,
        @PathVariable Integer projectId,
        @RequestBody @Valid AssignTaskRequestDto requestDto
    ) {
        taskService.assignTaskToUsersProject(userId, projectId, requestDto);
    }
}
