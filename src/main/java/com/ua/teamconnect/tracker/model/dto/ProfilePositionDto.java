package com.ua.teamconnect.tracker.model.dto;

public record ProfilePositionDto(
    Long id,
    String name,
    ProfileDepartmentDto department
) {
}
