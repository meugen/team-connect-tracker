package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DepartmentNotFoundException extends ResponseStatusException {
    public DepartmentNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, String.format("Department with id %d is not found", id));
    }
}
