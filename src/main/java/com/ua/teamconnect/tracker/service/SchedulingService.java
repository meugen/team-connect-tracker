package com.ua.teamconnect.tracker.service;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final HolidayService holidayService;

    @Scheduled(
        cron = "${team.connect.cron.holiday-sync}",
        zone = "UTC"
    )
    @SchedulerLock(
        name = "updateHolidaysNextYear",
        lockAtLeastFor = "1m"
    )
    public void updateHolidaysNextYear() {
        int year = Year.now().plusYears(1).getValue();
        holidayService.updateHolidaysInYear(year)
            .block();
    }
}
