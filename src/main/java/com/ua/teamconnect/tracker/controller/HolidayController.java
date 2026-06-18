package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.HolidayDto;
import com.ua.teamconnect.tracker.model.dto.HolidayRequestDto;
import com.ua.teamconnect.tracker.service.HolidayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday Controller", description = "Endpoints for managing holidays")
@ApiResponseOk
@ApiResponseUnauthorized
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping("/upcoming")
    @Tag(name = "Find Upcoming Holidays", description = "Returns the next five upcoming holidays starting from today.")
    public List<HolidayDto> findFiveUpcoming() {
        return holidayService.findFiveUpcoming();
    }

    @PostMapping
    @Tag(name = "Create Holiday", description = "Creates a new holiday.")
    public HolidayDto create(@Valid @RequestBody HolidayRequestDto dto) {
        return holidayService.create(dto);
    }

    @PutMapping(path = "/{holidayId}")
    @Tag(name = "Update Holiday", description = "Updates holiday.")
    public HolidayDto update(@PathVariable String holidayId, @Valid @RequestBody HolidayRequestDto dto) {
        return holidayService.update(holidayId, dto);
    }
}
