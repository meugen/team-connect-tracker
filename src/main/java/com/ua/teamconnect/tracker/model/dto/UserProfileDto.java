package com.ua.teamconnect.tracker.model.dto;

import com.ua.teamconnect.tracker.model.pojo.Phone;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfileDto(
    Long id,
    String firstName,
    String lastName,
    String avatar,
    String workEmail,
    LocalDateTime hireDate,
    String grade,
    Phone phones,
    List<StackDto> stacks,
    List<ProfilePositionDto> positions,
    List<ProfileProjectDto> projects,
    LocalDateTime birthDate
) {
}
