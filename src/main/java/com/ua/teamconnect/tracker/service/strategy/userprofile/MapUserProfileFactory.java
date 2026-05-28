package com.ua.teamconnect.tracker.service.strategy.userprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class MapUserProfileFactory {

    private static final Set<String> FULL_PROFILE_ROLES = Set.of(
        "HR", "PM", "ADMIN", "FINANCE"
    );

    @Qualifier("short")
    private final MapUserProfileStrategy shortUserProfileStrategy;
    @Qualifier("full")
    private final MapUserProfileStrategy fullUserProfileStrategy;

    public MapUserProfileStrategy full() {
        return fullUserProfileStrategy;
    }

    public MapUserProfileStrategy byRole(String role) {
        return FULL_PROFILE_ROLES.contains(role)
            ? fullUserProfileStrategy : shortUserProfileStrategy;
    }
}
