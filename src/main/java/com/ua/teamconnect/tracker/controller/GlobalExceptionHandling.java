package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import static com.ua.teamconnect.tracker.util.ExceptionHandlingUtil.buildUrl;
import java.time.LocalDateTime;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(
        IllegalArgumentException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDto(ex, buildUrl(request)));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var message = ex.getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .findFirst()
                        .orElse("Bad request");
        
        return ResponseEntity.badRequest()
                        .body(new ErrorDto(
                            HttpStatus.BAD_REQUEST.value(),
                            message,
                            LocalDateTime.now(),
                            buildUrl(request)
                        ));
    }
}
