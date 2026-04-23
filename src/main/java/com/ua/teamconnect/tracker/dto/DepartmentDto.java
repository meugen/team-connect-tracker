package com.ua.teamconnect.tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Department", description = "Data of the department")
public record DepartmentDto(
    @Schema(description = "Id of the department", example = "1")
    Long id,
    @Schema(description = "Name of the department", example = "Software Development")
    String name,
    @Schema(description = "Head Id of this department", example = "1")
    @JsonProperty("head_id")
    Long headId
) {
}
