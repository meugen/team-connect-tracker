package com.ua.teamconnect.tracker.model.dto;

import java.time.LocalDate;

public record TimeLogDto(
    Integer id,
    TaskDto task,
    ProjectDto project,
    LocalDate date,
    String description,
    Integer durationMinutes,
    String mediaFileUrl
) {
}
