package com.ua.teamconnect.tracker.model.dto;

import java.time.LocalDate;

public record ProfileProjectDto(
    Long id,
    String name,
    LocalDate startDate
) {
}
