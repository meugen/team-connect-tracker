package com.ua.teamconnect.tracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UploadedFileDto", description = "Data transfer object representing an uploaded file with its accessible URL")
public record UploadedFileDto(
    @Schema(description = "URL where the uploaded file can be accessed", example = "https://storage.example.com/files/example.pdf")
    String url
) {
}
