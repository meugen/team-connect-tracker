package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> handleResponseStatusException(
        ResponseStatusException ex,
        HttpServletRequest request
    ) {
        var url = request.getRequestURI();
        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }
        return ResponseEntity.status(ex.getStatusCode())
            .body(new ErrorDto(ex, url));
    }
}
