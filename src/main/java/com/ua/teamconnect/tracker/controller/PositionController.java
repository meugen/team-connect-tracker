package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseNotFound;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.PositionDto;
import com.ua.teamconnect.tracker.service.PositionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/positions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Position Controller", description = "Endpoints related to positions")
@RequiredArgsConstructor
@ApiResponseOk @ApiResponseUnauthorized
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    @ApiResponseNotFound
    public List<PositionDto> findAll(@RequestParam(required = false) Long departmentId) {
        return positionService.findAll(departmentId);
    }
}
