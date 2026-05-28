package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.HolidayDto;
import com.ua.teamconnect.tracker.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping("/upcoming")
    public List<HolidayDto> findUpcoming() {
        return holidayService.findUpcoming();
    }
}
