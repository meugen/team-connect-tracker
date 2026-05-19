package com.ua.teamconnect.tracker.util;

import com.ua.teamconnect.tracker.model.exception.InvalidMonthDayException;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

public interface DateUtil {

    static MonthDay toMonthDay(String value) {
        try {
            return MonthDay.parse(value, DateTimeFormatter.ofPattern("dd-MM"));
        } catch (Exception e) {
            throw new InvalidMonthDayException(value);
        }
    }
}
