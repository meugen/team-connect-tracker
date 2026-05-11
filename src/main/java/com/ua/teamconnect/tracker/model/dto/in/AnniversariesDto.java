package com.ua.teamconnect.tracker.model.dto.in;

import com.ua.teamconnect.tracker.model.exception.AnniversariesRequestException;
import com.ua.teamconnect.tracker.model.exception.DateWithoutYearException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static org.springframework.util.ObjectUtils.isEmpty;

public record AnniversariesDto(LocalDate startDate, LocalDate endDate) {

    public static AnniversariesDto of(String startDate, String endDate) {
        var start = toLocalDate(startDate);
        var end = toLocalDate(endDate);
        if (start.isAfter(end)) {
            throw new AnniversariesRequestException();
        }
        return new AnniversariesDto(start, end);
    }

    private static LocalDate toLocalDate(String date) {
        if (isEmpty(date)) throw new DateWithoutYearException(date);
        var pattern = Pattern.compile("^(\\d{2})-(\\d{2})$");
        var matcher = pattern.matcher(date);
        if (!matcher.find()) throw new DateWithoutYearException(date);
        var day = Integer.parseInt(matcher.group(1));
        var month = Integer.parseInt(matcher.group(2));
        try {
            // Important to use a leap year here, so 29th February counts as a valid date
            return LocalDate.of(2024, month, day);
        } catch (DateTimeException e) {
            throw new DateWithoutYearException(date);
        }
    }
}
