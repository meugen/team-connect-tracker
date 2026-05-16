package com.ua.teamconnect.tracker.model.dto;

public record UserDto(Integer id, String firstName, String lastName, String avatarUrl, ProfilePositionDto position) {
}
