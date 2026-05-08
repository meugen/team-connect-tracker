package com.ua.teamconnect.tracker.model.dto;

import java.time.LocalDate;

public record UserAnniversaryDto(
    Long id,
    String firstName,
    String lastName,
    String avatarUrl,
    LocalDate hireDate
) {
}
