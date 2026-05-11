package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AnniversariesRequestException extends ResponseStatusException {

    public AnniversariesRequestException() {
        super(HttpStatus.BAD_REQUEST, "Invalid date rage. Start date should be less or equal than end date");
    }
}
