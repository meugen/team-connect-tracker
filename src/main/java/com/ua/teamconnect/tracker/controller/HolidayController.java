package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.HolidayDto;
import com.ua.teamconnect.tracker.service.HolidayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
