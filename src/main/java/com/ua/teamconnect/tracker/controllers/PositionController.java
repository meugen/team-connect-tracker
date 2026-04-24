package com.ua.teamconnect.tracker.controllers;

import com.ua.teamconnect.tracker.dto.PositionDto;
import com.ua.teamconnect.tracker.services.PositionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/positions", produces = MediaType.APPLICATION_JSON_VALUE)
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
