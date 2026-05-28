package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Profile department", description = "Data for department in profile")
public record ProfileDepartmentDto(
    @Schema(description = "Department id", example = "1")
    Integer id,
    @Schema(description = "Department name", example = "Software development")
    String name
) {
}
