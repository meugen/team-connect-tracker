package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.PositionDto;
import com.ua.teamconnect.tracker.service.PositionService;
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
    public List<PositionDto> findAll(Optional<Long> departmentId) {
        return positionService.findAll(departmentId);
    }
}
