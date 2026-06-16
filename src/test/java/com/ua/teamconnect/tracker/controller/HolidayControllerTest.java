package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.Holiday;
import com.ua.teamconnect.tracker.repository.HolidayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HolidayControllerTest extends AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private HolidayRepository holidayRepository;

    @AfterEach
    void cleanUp() {
        holidayRepository.deleteAll();
    }

    private void setupData() {
        var holiday1 = new Holiday();
        holiday1.setId("holiday-1");
        holiday1.setName("Holiday 1");
        holiday1.setDescription("Holiday 1 description");
        holiday1.setDate(LocalDate.now().minusDays(2));
        holiday1.setIsDayOff(Boolean.TRUE);
        holidayRepository.save(holiday1);

        var holiday2 = new Holiday();
        holiday2.setId("holiday-2");
        holiday2.setName("Holiday 2");
        holiday2.setDescription("Holiday 2 description");
        holiday2.setDate(LocalDate.now().plusDays(2));
        holiday2.setIsDayOff(Boolean.TRUE);
        holidayRepository.save(holiday2);
    }


    @Test
    void findFiveUpcoming_valid_response() {
        setupData();
        setupValidToken();

        var expectedDate = LocalDate.now().plusDays(2);
        buildClient(port).get()
            .uri("/holidays/upcoming")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isEqualTo("holiday-2")
            .jsonPath("$[0].name").isEqualTo("Holiday 2")
            .jsonPath("$[0].description").isEqualTo("Holiday 2 description")
            .jsonPath("$[0].date").isEqualTo(expectedDate.toString())
            .jsonPath("$[0].dayOfWeek").isEqualTo(expectedDate.getDayOfWeek().toString());
    }

    @Test
    void findFiveUpcoming_invalidToken_unauthorized() {
        setupData();
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/holidays/upcoming")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    @Test
    void create_valid_response() {
        setupData();
        setupValidToken();

        var body = """
            {
              "name": "New holiday",
              "description": "New holiday description",
              "date": "2026-07-10",
              "isDayOff": true
            }
            """;
        buildClient(port).post()
            .uri("/holidays")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.name").isEqualTo("New holiday")
            .jsonPath("$.description").isEqualTo("New holiday description")
            .jsonPath("$.date").isEqualTo("2026-07-10")
            .jsonPath("$.dayOfWeek").isEqualTo("FRIDAY");
    }

    @Test
    void create_invalidToken_unauthorized() {
        setupData();
        setupValidToken();

        var body = """
            {
              "name": "New holiday",
              "description": "New holiday description",
              "date": "2026-07-10",
              "isDayOff": true
            }
            """;
        var spec = buildClient(port).post()
            .uri("/holidays")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateUnauthorized(spec);
    }

    @ParameterizedTest
    @MethodSource("invalidJson")
    void create_invalidJson_badRequest(String body) {
        setupData();
        setupValidToken();

        var spec = buildClient(port).post()
            .uri("/holidays")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
    }

    static List<Arguments> invalidJson() {
        return List.of(
            Arguments.of("""
                {
                  "description": "New holiday description",
                  "date": "2026-07-10",
                  "isDayOff": true
                }
                """
            ),
            Arguments.of("""
                {
                  "name": "New holiday",
                  "date": "2026-07-10",
                  "isDayOff": true
                }
                """
            ),
            Arguments.of("hgsdhsgbdhsds"),
            Arguments.of("""
                {
                  "name": "New holiday"
                  "description": "New holiday description",
                  "date": "nsbds",
                  "isDayOff": true
                }
                """
            )
        );
    }

    @Test
    void update_valid_response() {
        setupData();
        setupValidToken();

        var body = """
            {
              "name": "Updated holiday",
              "description": "Updated holiday description",
              "date": "2026-07-10",
              "isDayOff": true
            }
            """;
        buildClient(port).put()
            .uri("/holidays/holiday-1")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent()
            .expectBody().isEmpty();
    }

    @Test
    void update_invalidToken_unauthorized() {
        setupData();
        setupValidToken();

        var body = """
            {
              "name": "Updated holiday",
              "description": "Updated holiday description",
              "date": "2026-07-10",
              "isDayOff": true
            }
            """;
        var spec = buildClient(port).put()
            .uri("/holidays/holiday-1")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateUnauthorized(spec);
    }

    @ParameterizedTest
    @MethodSource("invalidJson")
    void update_invalidJson_badRequest(String body) {
        setupData();
        setupValidToken();

        var spec = buildClient(port).put()
            .uri("/holidays/holiday-1")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void update_invalidId_notFound() {
        setupData();
        setupValidToken();

        var body = """
            {
              "name": "Updated holiday",
              "description": "Updated holiday description",
              "date": "2026-07-10",
              "isDayOff": true
            }
            """;
        var spec = buildClient(port).put()
            .uri("/holidays/unknown")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateNotFound(spec);
    }
}
