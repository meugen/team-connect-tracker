package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {

    public UserNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, String.format("User with email %s is not found", email));
    }
}
