package com.ua.teamconnect.tracker.model.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record UserProfileDto(
    Long id,
    String firstName,
    String lastName,
    String avatar,
    String workEmail,
    LocalDate hireDate,
    String grade,
    Map<String, String> phones,
    List<StackDto> stacks,
    List<ProfilePositionDto> positions,
    List<ProfileProjectDto> projects,
    LocalDate birthDate
) {
}
