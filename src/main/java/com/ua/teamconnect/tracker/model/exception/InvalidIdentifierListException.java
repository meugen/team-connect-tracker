package com.ua.teamconnect.tracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidIdentifierListException extends ResponseStatusException {

    public InvalidIdentifierListException(String value) {
        super(HttpStatus.BAD_REQUEST, String.format("Value '%s' is not a valid list of identifiers", value));
    }
}
