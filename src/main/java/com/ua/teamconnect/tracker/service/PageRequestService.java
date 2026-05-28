package com.ua.teamconnect.tracker.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Map;

import static java.lang.Integer.parseInt;

public interface PageRequestService {

    String PARAM_PAGE = "page";
    String PARAM_SIZE = "size";
    String PARAM_SORT = "sort";
    String PARAM_ORDER = "order";

    default PageRequest pageRequestOf(Map<String, String> params) {
        var page = parseInt(params.getOrDefault(PARAM_PAGE, defaultPage())) - 1;
        var size = parseInt(params.getOrDefault(PARAM_SIZE, defaultSize()));
        var sort = convertToHqlField(
            params.getOrDefault(PARAM_SORT, defaultSort())
        );
        var order = params.getOrDefault(PARAM_ORDER, "asc");
        var direction = Sort.Direction.fromString(order);
        return PageRequest.of(page, size, direction, sort);
    }

    private String convertToHqlField(String sort) {
        var allowed = allowedSortProperties();
        var hqlField = allowed.get(sort);
        if (hqlField == null) {
            var keys = String.join(", ", allowed.keySet());
            throw new IllegalArgumentException(
                String.format("Invalid sort parameter: %s. Allowed values are: %s", sort, keys)
            );
        }
        return hqlField;
    }

    Map<String, String> allowedSortProperties();

    String defaultSort();

    default String defaultPage() {
        return "1";
    }

    default String defaultSize() {
        return "10";
    }
}
