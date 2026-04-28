package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Position", description = "Data of the position")
public record PositionDto(
    @Schema(description = "ID of the position", example = "1")
    Long id,
    @Schema(description = "Name of the position", example = "Software Engineer")
    String name,
    @Schema(description = "Department ID of the position", example = "1")
    Long departmentId
) {
}
