package com.ua.teamconnect.tracker.util;

import com.ua.teamconnect.tracker.model.exception.InvalidMonthDayException;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class DateUtil {

    private static final String DAY_MONTH_PATTERN = "dd-MM";
    private static final DateTimeFormatter DAY_MONTH_FORMATTER = DateTimeFormatter.ofPattern(DAY_MONTH_PATTERN);

    private DateUtil() {}

    private static MonthDay parse(String value) {
        try {
            return MonthDay.parse(value, DAY_MONTH_FORMATTER);
        } catch (Exception e) {
            throw new InvalidMonthDayException(value);
        }
    }

    public static List<Pair<MonthDay, MonthDay>> toDayMonthRanges(String startDate, String endDate) {
        var start = parse(startDate);
        var end = parse(endDate);
        return toRanges(start, end);
    }

    private static List<Pair<MonthDay, MonthDay>> toRanges(MonthDay start, MonthDay end) {
        if (start.equals(end) || start.isBefore(end)) {
            return List.of(new Pair<>(start, end));
        }
        return List.of(new Pair<>(start, MonthDay.of(Month.DECEMBER, 31)),
                        new Pair<>(MonthDay.of(Month.JANUARY, 1), end));
    }
}
