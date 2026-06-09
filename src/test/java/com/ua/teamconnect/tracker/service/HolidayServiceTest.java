package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.client.HolidayClient;
import com.ua.teamconnect.tracker.model.dto.api.calendarific.HolidaysList;
import com.ua.teamconnect.tracker.model.dto.api.calendarific.HolidaysListResponse;
import com.ua.teamconnect.tracker.model.dto.api.calendarific.HolidaysListResponseHoliday;
import com.ua.teamconnect.tracker.model.dto.api.calendarific.HolidaysListResponseHolidayDate;
import com.ua.teamconnect.tracker.model.entity.Holiday;
import com.ua.teamconnect.tracker.repository.HolidayRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class HolidayServiceTest {

    @MockitoBean
    private HolidayClient holidayClient;

    @MockitoBean
    private HolidayRepository holidayRepository;

    @Autowired
    private HolidayService holidayService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void updateHolidaysInYear_validYear_entitiesSaved() {
        var holidaysList = new HolidaysList(
            new HolidaysListResponse(List.of(
                new HolidaysListResponseHoliday(
                    "Holiday 1",
                    "Holiday 1 description",
                    "ukraine/holiday-1",
                    new HolidaysListResponseHolidayDate("2024-02-10")
                ),
                new HolidaysListResponseHoliday(
                    "Holiday 2",
                    "Holiday 2 description",
                    null,
                    new HolidaysListResponseHolidayDate("2024-10-02T10:03:20")
                )
            ))
        );
        when(holidayClient.fetchHolidaysInYear(2024)).thenReturn(Mono.just(holidaysList));

        holidayService.updateHolidaysInYear(2024).block();

        ArgumentCaptor<Iterable<Holiday>> holidaysCaptor = ArgumentCaptor.captor();
        verify(holidayRepository).saveAll(holidaysCaptor.capture());

        var holidays = holidaysCaptor.getValue();
        var iterator = holidays.iterator();
        var  holiday = iterator.next();
        assertEquals("-2055692786-2024-10-02T10:03:20", holiday.getId());
        assertEquals("Holiday 2", holiday.getName());
        assertEquals("Holiday 2 description", holiday.getDescription());
        assertEquals(LocalDate.of(2024, Month.OCTOBER, 2), holiday.getDate());
        holiday = iterator.next();
        assertEquals("ukraine/holiday-1-2024-02-10", holiday.getId());
        assertEquals("Holiday 1", holiday.getName());
        assertEquals("Holiday 1 description", holiday.getDescription());
        assertEquals(LocalDate.of(2024, Month.FEBRUARY, 10), holiday.getDate());
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}
