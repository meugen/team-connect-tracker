package com.ua.teamconnect.tracker.model.dto;

import java.time.LocalDateTime;

public record ProfileProjectDto(
    Long id,
    String name,
    LocalDateTime startDate
) {
}
