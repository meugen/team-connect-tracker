package com.ua.teamconnect.tracker.model.dto;

import com.ua.teamconnect.tracker.model.pojo.Gender;

import java.time.LocalDate;
import java.util.List;

public interface BasicUserInfo {
    Integer id();
    String firstName();
    String lastName();
    String workEmail();
    String avatar();
    LocalDate hireDate();
    String grade();
    Gender gender();
    List<StackDto> stacks();
    List<ProfilePositionDto> positions();
    List<ProfileProjectDto> projects();
}
