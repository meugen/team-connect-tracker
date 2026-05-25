package com.ua.teamconnect.tracker.model.entity.projection;

import com.ua.teamconnect.tracker.model.entity.Position;

import java.time.LocalDate;

public interface UserHireDate {
    Integer getUserId();
    String getFirstName();
    String getLastName();
    String getAvatarUrl();
    Position getPosition();
    LocalDate getHireDate();
}
