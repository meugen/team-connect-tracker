package com.ua.teamconnect.tracker.model.dto;

import java.util.List;

public record PageDto<T>(List<T> items, int totalPages, int currentPage, int totalItems) {
}
