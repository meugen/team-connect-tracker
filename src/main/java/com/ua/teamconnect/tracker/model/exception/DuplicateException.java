package com.ua.teamconnect.tracker.model.exception;

public class DuplicateException extends IllegalArgumentException {

    public static DuplicateException holiday() {
        return new DuplicateException("Holiday with the same name and date already exists");
    }

    public static DuplicateException task() {
        return new DuplicateException("Task with the same name already exists");
    }

    private DuplicateException(String message) {
        super(message);
    }
}
