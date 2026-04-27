package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.PositionDto;
import com.ua.teamconnect.tracker.service.PositionService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/positions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Position Controller", description = "Endpoints related to positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of positions"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Department with provided department ID is not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schemaProperties = {
                    @SchemaProperty(
                        name = "status",
                        schema = @Schema(
                            description = "HTTP status code",
                            example = "404",
                            type = "integer"
                        )
                    ),
                    @SchemaProperty(
                        name = "message",
                        schema = @Schema(
                            description = "Error message describing the reason for the error",
                            example = "Department with id 1 is not found",
                            type = "string"
                        )
                    ),
                    @SchemaProperty(
                        name = "url",
                        schema = @Schema(
                            description = "URL of the request that caused the error",
                            example = "/positions?departmentId=1",
                            type = "string"
                        )
                    ),
                    @SchemaProperty(
                        name = "timestamp",
                        schema = @Schema(
                            description = "Timestamp of when the error occurred",
                            example = "2024-06-01T12:00:00.123456",
                            type = "string",
                            format = "date-time"
                        )
                    )
                }
            )
        ),
    })
    public List<PositionDto> findAll(Optional<Long> departmentId) {
        return positionService.findAll(departmentId);
    }
}
