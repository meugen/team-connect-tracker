package com.ua.teamconnect.tracker.model.pojo;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.model.entity.UserProject;
import com.ua.teamconnect.tracker.model.entity.UserStack;

import java.time.LocalDate;
import java.util.List;

public record ProfileDetails(
    List<UserStack> stacks,
    List<UserProject> projects,
    List<UserPosition> positions,
    LocalDate hireDate
) {
}
