package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Profile position", description = "Data for position in profile")
public record ProfilePositionDto(
    @Schema(description = "Position id", example = "1")
    Long id,
    @Schema(description = "Position name", example = "Java Developer")
    String name,
    @Schema(description = "Position department")
    ProfileDepartmentDto department
) {
}
