package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserProjectAssignmentException extends ResponseStatusException {

    public UserProjectAssignmentException() {
        super(
            HttpStatus.BAD_REQUEST,
            "User is not assigned to the project or assignment is not active"
        );
    }
}
