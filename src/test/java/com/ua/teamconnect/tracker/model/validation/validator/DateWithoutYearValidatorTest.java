package com.ua.teamconnect.tracker.model.validation.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DateWithoutYearValidatorTest {

    private final DateWithoutYearValidator validator = new DateWithoutYearValidator();

    @ParameterizedTest
    @ValueSource(strings = {
        "10-01", "29-02", "31-12"
    })
    void isValid_valid_true(String value) {
        var context = mock(ConstraintValidatorContext.class);
        assertTrue(validator.isValid(value, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", "101-01", "30-02", "31-04", "asdas", "32-05"
    })
    void isValid_invalid_false(String value) {
        var context = mock(ConstraintValidatorContext.class);
        assertFalse(validator.isValid(value, context));
    }

}
