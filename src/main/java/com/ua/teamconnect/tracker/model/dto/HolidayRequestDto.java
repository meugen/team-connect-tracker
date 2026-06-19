package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Data Transfer Object for updating a holiday")
public record HolidayRequestDto(
    @Schema(description = "Name of the holiday", example = "New Year's Day")
    @NotEmpty(message = "Holiday name must not be null or empty")
    String name,
    @Schema(description = "Description of the holiday", example = "Celebration of the new year")
    @NotNull(message = "Holiday description must not be null")
    String description,
    @Schema(description = "Date of the holiday", example = "2026-01-01")
    @NotNull(message = "Holiday date must not be null")
    LocalDate date,
    @Schema(description = "Indicates if the holiday is a day off", example = "true")
    @NotNull(message = "Holiday day off status must not be null")
    Boolean isDayOff
) {
}
