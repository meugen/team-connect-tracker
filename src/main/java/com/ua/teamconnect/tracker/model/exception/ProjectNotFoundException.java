package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProjectNotFoundException extends ResponseStatusException {
    public ProjectNotFoundException(Integer projectId) {
        super(HttpStatus.NOT_FOUND, "Project with ID %d not found".formatted(projectId));
    }
}