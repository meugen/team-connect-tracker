package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import static com.ua.teamconnect.tracker.util.ExceptionHandlingUtil.buildUrl;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> handleResponseStatusException(
        ResponseStatusException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(ex.getStatusCode())
            .body(new ErrorDto(ex, buildUrl(request)));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDto> handleBindException(
        BindException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto(ex, buildUrl(request)));
    }
}
