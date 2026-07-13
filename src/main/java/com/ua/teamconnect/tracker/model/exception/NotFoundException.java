package com.ua.teamconnect.tracker.model.exception;

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

    private NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
