package com.ua.teamconnect.tracker.model.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;

public record HolidayDto(
    String id,
    String name,
    String description,
    LocalDate date,
    DayOfWeek dayOfWeek
) {
}
