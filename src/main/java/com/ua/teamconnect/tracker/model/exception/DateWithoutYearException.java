package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DateWithoutYearException extends ResponseStatusException {

    public DateWithoutYearException(String date) {
        super(HttpStatus.BAD_REQUEST, String.format("Invalid date without year: %s. Expected format: dd-MM", date));
    }
}
