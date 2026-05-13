package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(name = "User Profile", description = "Data for user profile")
public record UserProfileDto(
    @Schema(description = "User id", example = "1")
    Integer id,
    @Schema(description = "User first name", example = "John")
    String firstName,
    @Schema(description = "User last name", example = "Doe")
    String lastName,
    @Schema(description = "User avatar", example = "https://avatar.com")
    String avatar,
    @Schema(description = "User e-mail", example = "user@example.com")
    String workEmail,
    @Schema(description = "Date when user was hired", example = "2024-01-01")
    LocalDate hireDate,
    @Schema(description = "User grade", example = "Senior")
    String grade,
    @Schema(description = "User phones",
        example = "{\"home\": \"+123456789\", \"mobile\": \"+987654321\"}"
    )
    Map<String, String> phones,
    @Schema(description = "User stacks")
    List<StackDto> stacks,
    @Schema(description = "User positions")
    List<ProfilePositionDto> positions,
    @Schema(description = "User projects")
    List<ProfileProjectDto> projects,
    @Schema(description = "User birth date", example = "1990-10-05")
    LocalDate birthDate
) {
}
