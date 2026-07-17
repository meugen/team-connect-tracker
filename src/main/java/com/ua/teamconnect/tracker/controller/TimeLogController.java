package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.TimeLogDto;
import com.ua.teamconnect.tracker.service.TimeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/time-logs")
@RequiredArgsConstructor
public class TimeLogController {

    private final TimeLogService timeLogService;

    @GetMapping(params = "userId")
    @PreAuthorize("@securityProjectsValidator.validate(#userId, #root)")
    public List<TimeLogDto> findForUser(
        @RequestParam Integer userId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    ) {
        return timeLogService.findForUserId(userId, startDate, endDate);
    }

    @GetMapping(params = "!userId")
    public List<TimeLogDto> findForToken(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    ) {
        return timeLogService.findForUserEmail(jwt.getSubject(), startDate, endDate);
    }
}
