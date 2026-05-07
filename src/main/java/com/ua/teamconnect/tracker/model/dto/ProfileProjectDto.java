package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "Profile project", description = "Data for project in profile")
public record ProfileProjectDto(
    @Schema(description = "Project id", example = "1")
    Long id,
    @Schema(description = "Project name", example = "Team Connect")
    String name,
    @Schema(description = "Project start date", example = "2024-01-01")
    LocalDate startDate
) {
}
