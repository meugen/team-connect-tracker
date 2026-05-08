package com.ua.teamconnect.tracker.model.dto.in;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for AnniversariesDto validation order.
 * Verifies that validations execute in the correct sequence:
 * 1. BasicValidation (@NotNull, @NotEmpty)
 * 2. FormatValidation (@DateWithoutYear)
 * 3. Default group (@AnniversariesRequest)
 */
class AnniversariesDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== SCENARIO 1: Null values (BasicValidation fails) ==========

    @Test
    void validate_nullStartDate_failsOnBasicValidation() {
        var dto = new AnniversariesDto(null, "15-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // Should fail on @NotNull (BasicValidation group)
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("startDate", violation.getPropertyPath().toString());
        assertEquals("must not be null", violation.getMessage());

        // FormatValidation and AnniversariesRequest should NOT execute
        // (proven by only 1 violation, not multiple)
    }

    @Test
    void validate_nullEndDate_failsOnBasicValidation() {
        var dto = new AnniversariesDto("01-05", null);

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("endDate", violation.getPropertyPath().toString());
        assertEquals("must not be null", violation.getMessage());
    }

    @Test
    void validate_bothNull_failsOnBasicValidation() {
        var dto = new AnniversariesDto(null, null);

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // Should have 2 violations (one for each null field)
        // But since it's in BasicValidation group, it stops after this group
        assertEquals(2, violations.size());

        long nullViolations = violations.stream()
            .filter(v -> v.getMessage().equals("must not be null"))
            .count();
        assertEquals(2, nullViolations);
    }

    // ========== SCENARIO 2: Empty values (BasicValidation fails) ==========

    @Test
    void validate_emptyStartDate_failsOnBasicValidation() {
        var dto = new AnniversariesDto("", "15-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // Should fail on @NotEmpty (BasicValidation group)
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("startDate", violation.getPropertyPath().toString());
        assertEquals("must not be empty", violation.getMessage());

        // FormatValidation should NOT execute
    }

    @Test
    void validate_emptyEndDate_failsOnBasicValidation() {
        var dto = new AnniversariesDto("01-05", "");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("endDate", violation.getPropertyPath().toString());
        assertEquals("must not be empty", violation.getMessage());
    }

    // ========== SCENARIO 3: Invalid format (FormatValidation fails) ==========

    @Test
    void validate_invalidFormatStartDate_failsOnFormatValidation() {
        var dto = new AnniversariesDto("1-5", "15-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // BasicValidation passed, FormatValidation failed
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("startDate", violation.getPropertyPath().toString());
        assertEquals("Invalid date without year. Expected format: dd-MM", violation.getMessage());

        // AnniversariesRequest should NOT execute
    }

    @Test
    void validate_invalidFormatEndDate_failsOnFormatValidation() {
        var dto = new AnniversariesDto("01-05", "5-12");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("endDate", violation.getPropertyPath().toString());
        assertEquals("Invalid date without year. Expected format: dd-MM", violation.getMessage());
    }

    @Test
    void validate_invalidDate_failsOnFormatValidation() {
        var dto = new AnniversariesDto("31-02", "15-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // February 31st doesn't exist
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("startDate", violation.getPropertyPath().toString());
        assertEquals("Invalid date without year. Expected format: dd-MM", violation.getMessage());
    }

    @Test
    void validate_invalidMonth_failsOnFormatValidation() {
        var dto = new AnniversariesDto("15-13", "15-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // Month 13 doesn't exist
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("startDate", violation.getPropertyPath().toString());
        assertEquals("Invalid date without year. Expected format: dd-MM", violation.getMessage());
    }

    // ========== SCENARIO 4: Invalid date range (Cross-field validation fails) ==========

    @Test
    void validate_endDateBeforeStartDate_failsOnCrossFieldValidation() {
        var dto = new AnniversariesDto("15-05", "01-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // BasicValidation passed, FormatValidation passed, AnniversariesRequest failed
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("", violation.getPropertyPath().toString()); // Class-level constraint
        assertTrue(violation.getMessage().contains("Start date should be less or equal than end date"));
    }

    @Test
    void validate_endMonthBeforeStartMonth_failsOnCrossFieldValidation() {
        var dto = new AnniversariesDto("01-12", "15-06");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("Start date should be less or equal than end date"));
    }

    // ========== SCENARIO 5: Valid requests ==========

    @Test
    void validate_validSameDate_passes() {
        var dto = new AnniversariesDto("01-05", "01-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // All validations should pass
        assertEquals(0, violations.size());
    }

    @Test
    void validate_validSameMonthDifferentDay_passes() {
        var dto = new AnniversariesDto("01-05", "15-05");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_validDifferentMonth_passes() {
        var dto = new AnniversariesDto("25-05", "10-06");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_validLeapYearDate_passes() {
        var dto = new AnniversariesDto("29-02", "01-03");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // Feb 29 should be valid (validator uses leap year 2024)
        assertEquals(0, violations.size());
    }

    @Test
    void validate_validYearBoundary_passes() {
        var dto = new AnniversariesDto("01-01", "31-12");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        assertEquals(0, violations.size());
    }

    // ========== SCENARIO 6: Verify validation order (multiple errors) ==========

    @Test
    void validate_multipleErrors_onlyReportsFirstGroup() {
        // This has multiple issues:
        // 1. Empty startDate (BasicValidation)
        // 2. If it weren't empty, format would be invalid
        var dto = new AnniversariesDto("", "5-12");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // Should only report BasicValidation errors, not FormatValidation
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("must not be empty", violation.getMessage());
        // NOT "Invalid date without year" because FormatValidation doesn't execute
    }

    @Test
    void validate_formatAndRangeError_onlyReportsFormatError() {
        // This has:
        // 1. Invalid format on endDate (FormatValidation)
        // 2. Invalid range even if format was fixed
        var dto = new AnniversariesDto("15-05", "1-3");

        Set<ConstraintViolation<AnniversariesDto>> violations = validator.validate(dto);

        // Should only report FormatValidation error, not AnniversariesRequest
        assertEquals(1, violations.size());
        ConstraintViolation<AnniversariesDto> violation = violations.iterator().next();
        assertEquals("Invalid date without year. Expected format: dd-MM", violation.getMessage());
        // NOT the range error because AnniversariesRequest doesn't execute
    }
}

