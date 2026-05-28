package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Schema(description = "Data Transfer Object representing a holiday")
public record HolidayDto(
    @Schema(description = "Unique identifier of the holiday", example = "ukraine/new-year-2026-01-01")
    String id,
    @Schema(description = "Name of the holiday", example = "New Year's Day")
    String name,
    @Schema(description = "Description of the holiday", example = "Celebration of the new year")
    String description,
    @Schema(description = "Date of the holiday", example = "2026-01-01")
    LocalDate date,
    @Schema(description = "Day of the week for the holiday", example = "THURSDAY")
    DayOfWeek dayOfWeek
) {
}
