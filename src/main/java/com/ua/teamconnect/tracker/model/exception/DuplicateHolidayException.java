package com.ua.teamconnect.tracker.model.exception;

public class DuplicateHolidayException extends IllegalArgumentException {

    public DuplicateHolidayException() {
        super("Holiday with the same name and date already exists");
    }
}
