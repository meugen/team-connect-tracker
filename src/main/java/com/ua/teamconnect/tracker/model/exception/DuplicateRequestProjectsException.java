package com.ua.teamconnect.tracker.model.exception;

public class DuplicateRequestProjectsException extends IllegalArgumentException {

    public DuplicateRequestProjectsException(Integer projectId) {
        super("Duplicate project id in request: " + projectId);
    }
    
}
