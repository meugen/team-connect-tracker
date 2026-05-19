package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import static com.ua.teamconnect.tracker.util.ExceptionHandlingUtil.buildUrl;

@RestControllerAdvice
public class GlobalExceptionHandling {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandling.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> handleResponseStatusException(
        ResponseStatusException ex,
        HttpServletRequest request
    ) {
        LOGGER.error(ex.getReason(), ex);
        return ResponseEntity.status(ex.getStatusCode())
            .body(new ErrorDto(ex, buildUrl(request)));
    }
}
