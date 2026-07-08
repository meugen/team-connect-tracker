package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "TaskRequest", description = "Request payload for creating a new task")
public record TaskRequestDto(
    @Schema(description = "Name of the task", example = "Implement user authentication")
    @NotNull @NotEmpty String name,
    @Schema(description = "Description of the task", example = "Implement user authentication using JWT tokens")
    @NotNull @NotEmpty String description
) {
}
