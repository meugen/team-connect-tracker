package com.ua.teamconnect.tracker.model.pojo;

import java.util.Set;

public final class UserRole {

    public static final String HR = "HR";
    public static final String PM = "PM";
    public static final String ADMIN = "ADMIN";

    private static final Set<String> FULL_BIRTHDAY_ROLES = Set.of(HR, PM, ADMIN);

    private UserRole() {
    }

    public static boolean canSeeFullBirthDate(String role) {
        return FULL_BIRTHDAY_ROLES.contains(role);
    }
}
