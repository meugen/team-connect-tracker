package com.ua.teamconnect.tracker.model.dto;

import com.ua.teamconnect.tracker.model.pojo.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "Basic User Profile", description = "User profile data for users with role EMPLOYEE")
public record UserShortProfileDto(
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
    @Schema(description = "User gender", example = "MALE")
    Gender gender,
    @Schema(description = "User stacks")
    List<StackDto> stacks,
    @Schema(description = "User positions")
    List<ProfilePositionDto> positions,
    @Schema(description = "User projects")
    List<ProfileProjectDto> projects
) implements UserProfile {
}
