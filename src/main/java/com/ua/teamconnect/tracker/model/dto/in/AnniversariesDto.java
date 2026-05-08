package com.ua.teamconnect.tracker.model.dto.in;

import com.ua.teamconnect.tracker.model.validation.AnniversariesRequest;
import com.ua.teamconnect.tracker.model.validation.DateWithoutYear;
import com.ua.teamconnect.tracker.model.validation.group.BasicValidation;
import com.ua.teamconnect.tracker.model.validation.group.FormatValidation;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request DTO for anniversaries endpoint.
 * Validation order:
 * 1. BasicValidation: @NotNull, @NotEmpty
 * 2. FormatValidation: @DateWithoutYear
 * 3. Default (cross-field): @AnniversariesRequest
 */
@AnniversariesRequest
@GroupSequence({BasicValidation.class, FormatValidation.class, AnniversariesDto.class})
public record AnniversariesDto(
    @NotNull(groups = BasicValidation.class)
    @NotEmpty(groups = BasicValidation.class)
    @DateWithoutYear(groups = FormatValidation.class)
    String startDate,

    @NotNull(groups = BasicValidation.class)
    @NotEmpty(groups = BasicValidation.class)
    @DateWithoutYear(groups = FormatValidation.class)
    String endDate
) {

    private static Matcher matcher(String date) {
        var pattern = Pattern.compile("^(\\d{2})-(\\d{2})$");
        var matcher = pattern.matcher(date);
        @SuppressWarnings("unused")
        boolean found = matcher.find();
        return matcher;
    }

    public int startMonth() {
        return Integer.parseInt(matcher(startDate).group(2));
    }

    public int startDay() {
        return Integer.parseInt(matcher(startDate).group(1));
    }

    public int endMonth() {
        return Integer.parseInt(matcher(endDate).group(2));
    }

    public int endDay() {
        return Integer.parseInt(matcher(endDate).group(1));
    }
}
