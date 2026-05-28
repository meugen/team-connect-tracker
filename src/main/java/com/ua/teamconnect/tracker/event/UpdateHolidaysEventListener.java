package com.ua.teamconnect.tracker.event;

import com.ua.teamconnect.tracker.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@ConditionalOnBooleanProperty(prefix = "team.connect.calendarific", name = "update-on-startup", matchIfMissing = true)
@RequiredArgsConstructor
public class UpdateHolidaysEventListener {

    private final HolidayService holidayService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        holidayService.updateHolidaysInYear(Year.now().minusYears(1).getValue());
        holidayService.updateHolidaysInYear(Year.now().getValue());
        holidayService.updateHolidaysInYear(Year.now().plusYears(1).getValue());
    }
}
