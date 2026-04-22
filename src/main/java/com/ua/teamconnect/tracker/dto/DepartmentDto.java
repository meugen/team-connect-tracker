package com.ua.teamconnect.tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DepartmentDto(Long id, String name, @JsonProperty("head_id") Long headId) {
}
