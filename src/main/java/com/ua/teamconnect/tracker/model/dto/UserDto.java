package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User", description = "Basic user information")
public record UserDto(
    @Schema(description = "Unique identifier of the user", example = "1")
    Integer id,
    @Schema(description = "User's first name", example = "John")
    String firstName,
    @Schema(description = "User's last name", example = "Doe")
    String lastName,
    @Schema(description = "User's avatar URL address", example = "https://avatar.com/avatar.png")
    String avatarUrl,
    @Schema(description = "User's position in the company")
    ProfilePositionDto position
) {
}
