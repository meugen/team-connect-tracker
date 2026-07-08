package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Task", description = "Task resource")
public record TaskDto(
    @Schema(description = "Unique identifier of the task", example = "1")
    Integer id,
    @Schema(description = "Name of the task", example = "Implement user authentication")
    String name,
    @Schema(description = "Description of the task", example = "Implement user authentication using JWT tokens")
    String description
) {
}
