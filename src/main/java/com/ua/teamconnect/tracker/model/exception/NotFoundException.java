package com.ua.teamconnect.tracker.model.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFoundException extends ResponseStatusException {

    public static NotFoundException department(Integer id) {
        return new NotFoundException("Department with id %d is not found".formatted(id));
    }

    public static NotFoundException holiday(String id) {
        return new NotFoundException("Holiday with id '%s' is not found".formatted(id));
    }

    public static NotFoundException task(Integer id) {
        return new NotFoundException("Task with id %d not found".formatted(id));
    }

    public static NotFoundException userById(Integer id) {
        return new NotFoundException("User with id %d is not found".formatted(id));
    }

    public static NotFoundException userByEmail(String email) {
        return new NotFoundException("User with email %s is not found".formatted(email));
    }

    public static NotFoundException project(Integer id) {
        return new NotFoundException("Project with ID %d not found".formatted(id));
    }
    
    public static NotFoundException projects(List<Integer> ids) {
        return new NotFoundException("Projects with IDs %s not found".formatted(ids));
    }
    
    private NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
