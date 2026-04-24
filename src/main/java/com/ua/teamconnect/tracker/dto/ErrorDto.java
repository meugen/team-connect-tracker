package com.ua.teamconnect.tracker.dto;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

public record ErrorDto(int status, String message, LocalDateTime timestamp, String url) {

    public ErrorDto(ResponseStatusException ex, String url) {
        this(ex.getStatusCode().value(), ex.getReason(), LocalDateTime.now(), url);
    }
}
