package com.ua.teamconnect.tracker.model.entity.projection;

import java.time.LocalDate;

public interface Anniversary {
    Integer getUserId();
    String getFirstName();
    String getLastName();
    String getAvatarUrl();
    LocalDate getHireDate();
}
