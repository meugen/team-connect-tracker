package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

public record ErrorDto(
    @Schema(description = "HTTP status code", example = "404")
    int status,
    @Schema(description = "Error message describing the reason for the error", example = "Resource with id 1 is not found")
    String message,
    @Schema(description = "Timestamp of when the error occurred", example = "2024-06-01T12:00:00.123456", type = "string", format = "date-time")
    LocalDateTime timestamp,
    @Schema(description = "URL of the request that caused the error", example = "/resources?id=1")
    String url
) {

    public ErrorDto(ResponseStatusException ex, String url) {
        this(ex.getStatusCode().value(), ex.getReason(), LocalDateTime.now(), url);
    }

    public ErrorDto(AuthenticationException ex, String url) {
        this(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), LocalDateTime.now(), url);
    }

    public ErrorDto(IllegalArgumentException ex, String url) {
        this(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now(), url);
    }
}
