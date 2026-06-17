package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidMonthDayException extends ResponseStatusException {

    public InvalidMonthDayException(String date) {
        super(HttpStatus.BAD_REQUEST, String.format("Invalid month day: '%s'. Required format: dd-MM", date));
    }
}
