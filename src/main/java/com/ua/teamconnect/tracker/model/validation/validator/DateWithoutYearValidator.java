package com.ua.teamconnect.tracker.model.validation.validator;

import com.ua.teamconnect.tracker.model.validation.DateWithoutYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class DateWithoutYearValidator implements ConstraintValidator<DateWithoutYear, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var pattern = Pattern.compile("^(\\d{2})-(\\d{2})$");
        var matcher = pattern.matcher(value);
        if (!matcher.find()) return false;
        var day = Integer.parseInt(matcher.group(1));
        var month = Integer.parseInt(matcher.group(2));
        try {
            // Important to use a leap year here, so 29th February counts as a valid date
            @SuppressWarnings("unused")
            var date = LocalDate.of(2024, month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
