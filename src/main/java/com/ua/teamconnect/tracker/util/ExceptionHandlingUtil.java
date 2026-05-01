package com.ua.teamconnect.tracker.util;

import jakarta.servlet.http.HttpServletRequest;

public interface ExceptionHandlingUtil {

    static String buildUrl(HttpServletRequest request) {
        var url = request.getRequestURI();
        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }
        return url;
    }
}
