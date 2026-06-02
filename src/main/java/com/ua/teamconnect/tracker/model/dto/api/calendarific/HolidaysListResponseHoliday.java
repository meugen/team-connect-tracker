package com.ua.teamconnect.tracker.model.dto.api.calendarific;

import java.time.LocalDate;
import java.util.Objects;

public record HolidaysListResponseHoliday(
    String name,
    String description,
    String urlid,
    HolidaysListResponseHolidayDate date
) {

    public String genId() {
        var nonNullId = urlid == null ? Objects.hash(name, description, date.iso()) + "" : urlid;
        return nonNullId + "-" + date.iso();
    }

    public LocalDate parseLocalDate() {
        return LocalDate.parse(date.iso().split("T")[0]);
    }
}
