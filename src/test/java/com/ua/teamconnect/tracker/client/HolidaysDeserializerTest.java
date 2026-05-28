package com.ua.teamconnect.tracker.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HolidaysDeserializerTest {

    private final HolidaysDeserializer deserializer = new HolidaysDeserializer();

    @Test
    void deserialize_validJson_validEntityList() {
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

        var factory = new JsonFactory(new ObjectMapper());
        var holidays = assertDoesNotThrow(() -> {
            try (JsonParser parser = factory.createParser(json)) {
                return deserializer.deserialize(parser, null).holidays();
            }
        });
        assertEquals(3, holidays.size());
        var iterator = holidays.iterator();
        var holiday3 = iterator.next();
        assertEquals("-1424651880", holiday3.getId());
        assertEquals("Holiday 3", holiday3.getName());
        assertEquals("Holiday 3 description", holiday3.getDescription());
        assertEquals(LocalDate.of(2022, Month.AUGUST, 10), holiday3.getDate());

        var holiday1 = iterator.next();
        assertEquals("ukraine/holiday-1-2018-12-31", holiday1.getId());
        assertEquals("Holiday 1", holiday1.getName());
        assertEquals("Holiday 1 description", holiday1.getDescription());
        assertEquals(LocalDate.of(2018, Month.DECEMBER, 31), holiday1.getDate());

        var holiday2 = iterator.next();
        assertEquals("ukraine/holiday-2-2020-05-20", holiday2.getId());
        assertEquals("Holiday 2", holiday2.getName());
        assertEquals("Holiday 2 description", holiday2.getDescription());
        assertEquals(LocalDate.of(2020, Month.MAY, 20), holiday2.getDate());
    }

    @ParameterizedTest
    @MethodSource("invalidJsonProvider")
    void deserialize_invalidJson_throwsException(String json) {
        var factory = new JsonFactory(new ObjectMapper());
        assertThrows(Exception.class, () -> {
            try (JsonParser parser = factory.createParser(json)) {
                deserializer.deserialize(parser, null);
            }
        });
    }

    static List<Arguments> invalidJsonProvider() {
        return List.of(
            Arguments.of("""
                {
                  "meta": {
                    "code": 200
                  },
                  "response": null
                }
                """),
            Arguments.of("""
                {
                  "meta": {
                    "code": 200
                  },
                  "response": {
                    "holidays": null
                  }
                }
                """),
            Arguments.of("Invalid JSON")
        );
    }
}
