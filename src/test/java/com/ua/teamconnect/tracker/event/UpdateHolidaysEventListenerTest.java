package com.ua.teamconnect.tracker.event;

import com.ua.teamconnect.tracker.service.HolidayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UpdateHolidaysEventListenerTest {

    private HolidayService holidayService;
    private UpdateHolidaysEventListener listener;

    @BeforeEach
    void setUp() {
        holidayService = mock(HolidayService.class);
        listener = new UpdateHolidaysEventListener(holidayService);
    }

    @Test
    void onApplicationReady_holidaysUpdated() {
        listener.onApplicationReady();

        var year = Year.now();
        verify(holidayService).updateHolidaysInYear(year.minusYears(1).getValue());
        verify(holidayService).updateHolidaysInYear(year.getValue());
        verify(holidayService).updateHolidaysInYear(year.plusYears(1).getValue());
    }
}
