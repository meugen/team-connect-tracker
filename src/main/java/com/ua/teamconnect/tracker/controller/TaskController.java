package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.TaskDto;
import com.ua.teamconnect.tracker.model.dto.TaskRequestDto;
import com.ua.teamconnect.tracker.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping(path = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "Endpoints related to tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Tag(name = "Create Task", description = "Creates a new task")
    public TaskDto create(@RequestBody @Valid TaskRequestDto requestDto) {
        return taskService.create(requestDto);
    }
}
