package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnsupportedFilterException extends ResponseStatusException {

    public UnsupportedFilterException(String filter) {
        super(HttpStatus.BAD_REQUEST, String.format("Unsupported filter: %s", filter));
    }

}
