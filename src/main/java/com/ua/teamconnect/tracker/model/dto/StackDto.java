package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Stack", description = "Data of the stack")
public record StackDto(
    @Schema(description = "Id of the stack", example = "1")
    Long id,
    @Schema(description = "Name of the stack", example = "Java")
    String name
) {
}
