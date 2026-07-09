package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TaskNotFoundException extends ResponseStatusException {

    public TaskNotFoundException(Integer taskId) {
        super(HttpStatus.NOT_FOUND, "Task with id %d not found".formatted(taskId));
    }
}
