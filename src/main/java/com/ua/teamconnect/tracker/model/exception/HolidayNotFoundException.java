package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HolidayNotFoundException extends ResponseStatusException {

    public HolidayNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Holiday with id '%s' is not found".formatted(id));
    }
}
