package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "Page", description = "Paged resource")
public record PageDto<T>(
    @Schema(description = "List of items on the current page")
    List<T> items,
    @Schema(description = "Total number of pages available", example = "5")
    int totalPages,
    @Schema(description = "Current page number (1-indexed)", example = "1")
    int currentPage,
    @Schema(description = "Number of items per page", example = "10")
    int totalItems
) {
}
