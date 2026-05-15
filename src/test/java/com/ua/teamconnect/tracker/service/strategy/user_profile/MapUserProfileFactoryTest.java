package com.ua.teamconnect.tracker.service.strategy.user_profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

class MapUserProfileFactoryTest {

    private MapUserProfileFactory factory;

    @BeforeEach
    void prepareFactory() {
        var fullMapStrategy = mock(MapFullUserProfileStrategy.class);
        var shortMapStrategy = mock(MapShortUserProfileStrategy.class);
        this.factory = new MapUserProfileFactory(shortMapStrategy, fullMapStrategy);
    }

    @Test
    void full_always_fullMapStrategy() {
        var result = factory.full();
        assertInstanceOf(MapFullUserProfileStrategy.class, result);
    }

    @Test
    void byRole_roleEngineer_shortMapStrategy() {
        var result = factory.byRole("ENGINEER");
        assertInstanceOf(MapShortUserProfileStrategy.class, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"HR", "PM", "ADMIN", "FINANCE"})
    void byRole_roleAdmin_fullMapStrategy(String role) {
        var result = factory.byRole(role);
        assertInstanceOf(MapFullUserProfileStrategy.class, result);
    }
}
