package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User Birth Date", description = "User information with birth date")
public record UserBirthdayDto(

        @Schema(description = "User ID", example = "1")
        Integer id,

        @Schema(description = "User's first name", example = "John")
        String firstName,

        @Schema(description = "User's last name", example = "Doe")
        String lastName,

        @Schema(description = "User's avatar URL", example = "https://example.com/avatar.jpg")
        String avatar,

        @Schema(description = "User's birth date (MM-dd for employees, MM-dd-yyyy for HR/PM/ADMIN)", example = "06-15")
        String birthDate

) {
}

