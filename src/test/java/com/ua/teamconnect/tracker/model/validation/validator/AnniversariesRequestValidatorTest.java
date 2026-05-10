package com.ua.teamconnect.tracker.model.validation.validator;

import com.ua.teamconnect.tracker.model.dto.in.AnniversariesDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class AnniversariesRequestValidatorTest {

    private final AnniversariesRequestValidator validator = new AnniversariesRequestValidator();

    @ParameterizedTest
    @MethodSource("provideValidDates")
    void isValid_valid_true(String startDate, String endDate) {
        var dto = new AnniversariesDto(startDate, endDate);
        var context = mock(ConstraintValidatorContext.class);
        assertTrue(validator.isValid(dto, context));
    }

    private static List<Arguments> provideValidDates() {
        return List.of(
            Arguments.of("10-01", "01-02"),
            Arguments.of("10-01", "20-01"),
            Arguments.of("10-01", "10-01")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDates")
    void isValid_invalid_false(String startDate, String endDate) {
        var dto = new AnniversariesDto(startDate, endDate);
        var context = mock(ConstraintValidatorContext.class);
        assertFalse(validator.isValid(dto, context));
    }

    private static List<Arguments> provideInvalidDates() {
        return List.of(
            Arguments.of("01-02", "10-01"),
            Arguments.of("20-01", "10-01")
        );
    }
}
