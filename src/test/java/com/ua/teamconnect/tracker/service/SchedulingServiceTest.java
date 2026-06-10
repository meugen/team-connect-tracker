package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.extension.ShedLockExtension;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.Year;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "team.connect.cron.holiday-sync=* * * * * *"
    }
)
@ExtendWith(ShedLockExtension.class)
class SchedulingServiceTest {

    @MockitoBean
    private HolidayService holidayService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void updateHolidaysNextYear_executed_withNextYear() {
        var year = Year.now().plusYears(1).getValue();
        Awaitility.await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> verify(holidayService, atLeastOnce()).updateHolidaysInYear(year));
    }
}
