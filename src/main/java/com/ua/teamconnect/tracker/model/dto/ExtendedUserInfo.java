package com.ua.teamconnect.tracker.model.dto;

import java.time.LocalDate;
import java.util.Map;

public interface ExtendedUserInfo extends BasicUserInfo {

    Map<String, String> phones();
    LocalDate birthDate();
}
