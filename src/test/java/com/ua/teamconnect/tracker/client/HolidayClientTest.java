package com.ua.teamconnect.tracker.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class HolidayClientTest {

    private MockWebServer mockWebServer;
    private HolidayClient holidayClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        holidayClient = new HolidayClient(
            WebClient.builder(),
            "calendarific-api-key",
            mockWebServer.url("/api/v2").toString()
        );
    }

    @Test
    void fetchHolidaysInYear_validResponse_validHolidaySet() {
        var json = """
            {
              "meta": {
                "code": 200
              },
              "response": {
                "holidays": [
                  {
                    "name": "Holiday 1",
                    "description": "Holiday 1 description",
                    "date": {
                      "iso": "2018-12-31",
                      "datetime": {
                        "year": 2018,
                        "month": 12,
                        "day": 31
                      }
                    },
                    "urlid": "ukraine/holiday-1"
                  },
                  {
                    "name": "Holiday 2",
                    "description": "Holiday 2 description",
                    "date": {
                      "iso": "2020-05-20T12:00:10.123456",
                      "datetime": {
                        "year": 2020,
                        "month": 5,
                        "day": 20,
                        "hour": 12,
                        "minute": 0,
                        "second": 10
                      }
                    },
                    "urlid": "ukraine/holiday-2"
                  },
                  {
                    "name": "Holiday 3",
                    "description": "Holiday 3 description",
                    "date": {
                      "iso": "2022-08-10",
                      "datetime": {
                        "year": 2022,
                        "month": 8,
                        "day": 10
                      }
                    },
                    "urlid": null
                  }
                ]
              }
            }
            """;
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(json)
            .addHeader("Content-Type", "application/json"));

        var holidays = assertDoesNotThrow(() ->
            holidayClient.fetchHolidaysInYear(2020).block().response().holidays()
        );

        assertNotNull(holidays);
        assertEquals(3, holidays.size());
        var iterator = holidays.iterator();
        var holiday1 = iterator.next();
        assertEquals("ukraine/holiday-1", holiday1.urlid());
        assertEquals("Holiday 1", holiday1.name());
        assertEquals("Holiday 1 description", holiday1.description());
        assertEquals("2018-12-31", holiday1.date().iso());

        var holiday2 = iterator.next();
        assertEquals("ukraine/holiday-2", holiday2.urlid());
        assertEquals("Holiday 2", holiday2.name());
        assertEquals("Holiday 2 description", holiday2.description());
        assertEquals("2020-05-20T12:00:10.123456", holiday2.date().iso());

        var holiday3 = iterator.next();
        assertNull(holiday3.urlid());
        assertEquals("Holiday 3", holiday3.name());
        assertEquals("Holiday 3 description", holiday3.description());
        assertEquals("2022-08-10", holiday3.date().iso());
    }
}
