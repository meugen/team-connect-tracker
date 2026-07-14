package com.ua.teamconnect.tracker.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AssignTaskRequestDto(@NotNull Set<Integer> taskIds) {
}
