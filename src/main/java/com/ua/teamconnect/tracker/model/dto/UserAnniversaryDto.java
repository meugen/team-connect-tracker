package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "User Anniversary", description = "User anniversary information")
public record UserAnniversaryDto(
    @Schema(description = "User ID", example = "1")
    Integer id,
    @Schema(description = "User's first name", example = "John")
    String firstName,
    @Schema(description = "User's last name", example = "Doe")
    String lastName,
    @Schema(description = "User's avatar URL", example = "https://example.com/avatar.jpg")
    String avatarUrl,
    @Schema(description = "User's hire date", example = "2020-01-15")
    LocalDate hireDate
) {
}
