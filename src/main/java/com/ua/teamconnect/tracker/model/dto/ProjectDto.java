package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Project", description = "Information about project")
public record ProjectDto(
    @Schema(description = "Project ID", example = "1")
    Integer id,
    @Schema(description = "Project name", example = "Team Connect")
    String name,
    @Schema(description = "Project description", example = "HR system for the company")
    String description
) {
}
