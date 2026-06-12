package com.ua.teamconnect.tracker.util;

import com.ua.teamconnect.tracker.model.exception.InvalidMonthDayException;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class DateUtil {
    
    private static final String DAY_MONTH_PATTERN = "dd-MM";
    private static final String MONTH_DAY_PATTERN = "MM-dd";
    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern(DAY_MONTH_PATTERN);
    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern(MONTH_DAY_PATTERN);
    
    private DateUtil() {
    }

    public static MonthDay parse(String value, DateTimeFormatter formatter, String patern) {
        try {
            return MonthDay.parse(value, formatter);
        } catch (Exception e) {
            throw new InvalidMonthDayException(value, patern);
        }
    }
    
    public static List<Pair<MonthDay, MonthDay>> toMonthDayRanges(String startDate, String endDate) {
        var start = parse(startDate, MONTH_DAY, MONTH_DAY_PATTERN);
        var end = parse(endDate, MONTH_DAY, MONTH_DAY_PATTERN);
        return toRanges(start, end);
    }

    public static List<Pair<MonthDay, MonthDay>> toDayMonthRanges(String startDate, String endDate) {
        var start = parse(startDate, DAY_MONTH, DAY_MONTH_PATTERN);
        var end = parse(endDate, DAY_MONTH, DAY_MONTH_PATTERN);
        return toRanges(start, end);
    }

    private static List<Pair<MonthDay, MonthDay>> toRanges(MonthDay start, MonthDay end) {
        if (start.equals(end) || start.isBefore(end)) {
            return List.of(new Pair<>(start, end));
        }
        return List.of(
            new Pair<>(start, MonthDay.of(Month.DECEMBER, 31)),
            new Pair<>(MonthDay.of(Month.JANUARY, 1), end)
        );
    }
}
