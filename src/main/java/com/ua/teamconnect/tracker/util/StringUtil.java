package com.ua.teamconnect.tracker.util;

import com.ua.teamconnect.tracker.model.exception.InvalidIdentifierListException;

import java.util.Arrays;
import java.util.Set;

public interface StringUtil {

    static Set<Integer> parseIntSet(String value) {
        try {
            return Arrays.stream(value.split(","))
                .map(Integer::parseInt)
                .collect(java.util.stream.Collectors.toSet());
        } catch (Exception e) {
            throw new InvalidIdentifierListException(value);
        }
    }

    static String escapeForLike(String value) {
        return value.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    }
}
